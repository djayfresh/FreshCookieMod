package com.djayfresh.freshcaa.entity.task;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.djayfresh.freshcaa.block.entity.SunDryingTableBlockEntity;
import com.djayfresh.freshcaa.registry.ModTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

/**
 * Item movement for workers, all through the NeoForge transfer API so chests, furnaces, the Sun Drying Table and
 * modded inventories behave the same. Machines are fed through their top face and emptied through their bottom face,
 * which is what hoppers do; fuel goes in through a side face.
 */
public final class WorkerTransfers {
    private static final Direction[] INSERT_SIDES = {Direction.UP, Direction.NORTH, null};
    private static final Direction[] FUEL_INSERT_SIDES = {Direction.NORTH, Direction.UP, null};
    private static final Direction[] EXTRACT_SIDES = {Direction.DOWN, null};

    private WorkerTransfers() {}

    public static @Nullable ResourceHandler<ItemResource> handler(Level level, BlockPos pos, @Nullable Direction side) {
        if (!level.isLoaded(pos)) {
            return null;
        }
        return level.getCapability(Capabilities.Item.BLOCK, pos, side);
    }

    /** True if any item handler lives at the position. */
    public static boolean isContainer(Level level, BlockPos pos) {
        return handler(level, pos, null) != null;
    }

    /** True if the block has a block entity and an item handler: the cheap pre-filter for area scans. */
    public static boolean looksLikeContainer(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && isContainer(level, pos);
    }

    /** The handler a worker takes items out of (bottom face first, so furnaces give up their results, not their fuel). */
    public static @Nullable ResourceHandler<ItemResource> extractHandler(Level level, BlockPos pos) {
        for (Direction side : EXTRACT_SIDES) {
            ResourceHandler<ItemResource> handler = handler(level, pos, side);
            if (handler != null) {
                return handler;
            }
        }
        return null;
    }

    /** Whether at least one matching item can be pulled from the handler. Nothing is moved. */
    public static boolean hasExtractable(@Nullable ResourceHandler<ItemResource> handler, Predicate<ItemResource> filter) {
        if (handler == null) {
            return false;
        }
        try (Transaction tx = Transaction.openRoot()) {
            ResourceStack<ItemResource> stack = ResourceHandlerUtil.extractFirst(handler, filter, 1, tx);
            return stack != null && !stack.isEmpty();
        }
    }

    /** One of the first matching item in the handler, or empty. Nothing is moved. */
    public static ItemStack peekFirst(@Nullable ResourceHandler<ItemResource> handler, Predicate<ItemResource> filter) {
        if (handler == null) {
            return ItemStack.EMPTY;
        }
        try (Transaction tx = Transaction.openRoot()) {
            ResourceStack<ItemResource> stack = ResourceHandlerUtil.extractFirst(handler, filter, 1, tx);
            return stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack.resource().toStack(1);
        }
    }

    /**
     * Moves up to {@code max} of the first matching item type from the handler into the worker's carry inventory.
     *
     * @return how many items moved
     */
    public static int pickUp(@Nullable ResourceHandler<ItemResource> from, Container carry, Predicate<ItemResource> filter, int max) {
        if (from == null) {
            return 0;
        }
        ResourceStack<ItemResource> moved = ResourceHandlerUtil.moveFirstStacking(from, VanillaContainerWrapper.of(carry), filter, max, null);
        int count = moved == null ? 0 : moved.amount();
        if (count > 0) {
            carry.setChanged();
        }
        return count;
    }

    /**
     * Pushes everything in the carry inventory into the block at {@code pos}. Fuel-like items try a side face first so
     * they land in a furnace's fuel slot; everything else goes in through the top.
     *
     * @return how many items moved
     */
    public static int deliver(Level level, BlockPos pos, Container carry) {
        int total = 0;
        for (int slot = 0; slot < carry.getContainerSize(); slot++) {
            ItemStack stack = carry.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            Direction[] sides = stack.has(DataComponents.COOKING_FUEL) ? FUEL_INSERT_SIDES : INSERT_SIDES;
            for (Direction side : sides) {
                ResourceHandler<ItemResource> handler = handler(level, pos, side);
                if (handler == null) {
                    continue;
                }
                int inserted;
                try (Transaction tx = Transaction.openRoot()) {
                    inserted = ResourceHandlerUtil.insertStacking(handler, ItemResource.of(stack), stack.getCount(), tx);
                    tx.commit();
                }
                if (inserted > 0) {
                    stack.shrink(inserted);
                    total += inserted;
                    if (stack.isEmpty()) {
                        carry.setItem(slot, ItemStack.EMPTY);
                        break;
                    }
                }
            }
        }
        if (total > 0) {
            carry.setChanged();
        }
        return total;
    }

    /** Whether the block at {@code pos} would take at least one of the given item through any insert face. */
    public static boolean canAccept(Level level, BlockPos pos, ItemStack sample) {
        if (sample.isEmpty()) {
            return isContainer(level, pos);
        }
        Direction[] sides = sample.has(DataComponents.COOKING_FUEL) ? FUEL_INSERT_SIDES : INSERT_SIDES;
        for (Direction side : sides) {
            ResourceHandler<ItemResource> handler = handler(level, pos, side);
            if (handler == null) {
                continue;
            }
            try (Transaction tx = Transaction.openRoot()) {
                if (ResourceHandlerUtil.insertStacking(handler, ItemResource.of(sample), 1, tx) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Whether the block at {@code pos} actually has a use for the item and room for it. Furnaces, smokers and blast
     * furnaces only want items they can cook or burn (their top slot accepts anything, so a plain insert test would let
     * junk in); the Sun Drying Table only wants dryable items; any other container wants whatever fits.
     */
    public static boolean wants(Level level, BlockPos pos, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        BlockEntity blockEntity = level.isLoaded(pos) ? level.getBlockEntity(pos) : null;
        if (blockEntity instanceof AbstractFurnaceBlockEntity) {
            boolean fuel = stack.has(DataComponents.COOKING_FUEL);
            if (!fuel && !canCook(level, blockEntity, stack)) {
                return false;
            }
        } else if (blockEntity instanceof SunDryingTableBlockEntity && !stack.is(ModTags.Items.SUN_DRYABLE)) {
            return false;
        }
        return canAccept(level, pos, stack);
    }

    private static boolean canCook(Level level, BlockEntity furnace, ItemStack stack) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        RecipeType<? extends AbstractCookingRecipe> type = furnace instanceof BlastFurnaceBlockEntity ? RecipeType.BLASTING
                : furnace instanceof SmokerBlockEntity ? RecipeType.SMOKING : RecipeType.SMELTING;
        return cook(serverLevel, type, stack);
    }

    private static <T extends AbstractCookingRecipe> boolean cook(ServerLevel level, RecipeType<T> type, ItemStack stack) {
        return level.recipeAccess().getRecipeFor(type, new SingleRecipeInput(stack), level).isPresent();
    }

    public static Predicate<ItemResource> filterFor(ItemStack sample) {
        if (sample.isEmpty()) {
            return resource -> true;
        }
        return resource -> resource.matches(sample);
    }
}
