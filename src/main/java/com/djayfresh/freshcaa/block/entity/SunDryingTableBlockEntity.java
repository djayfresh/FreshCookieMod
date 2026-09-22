package com.djayfresh.freshcaa.block.entity;

import com.djayfresh.freshcaa.block.SunDryingTableBlock;
import com.djayfresh.freshcaa.menu.SunDryingTableMenu;
import com.djayfresh.freshcaa.registry.ModBlockEntities;
import com.djayfresh.freshcaa.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * Two-slot container (input, result). While the block is in direct daytime sunlight it applies the input's
 * furnace recipe, restricted to items tagged {@code freshcaa:sun_dryable}. No fuel is ever consumed.
 */
public class SunDryingTableBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_RESULT = 1;
    public static final int SLOT_COUNT = 2;

    public static final int DATA_PROGRESS = 0;
    public static final int DATA_TOTAL = 1;
    public static final int DATA_IN_SUN = 2;
    public static final int DATA_COUNT = 3;

    /** Ticks of sunlight needed per item. The 1.6.4 table used half a furnace cycle. */
    public static final int DRY_TIME = 100;
    private static final int MIN_SKY_LIGHT = 14;

    private static final int[] SLOTS_FOR_UP = new int[]{SLOT_INPUT};
    private static final int[] SLOTS_FOR_DOWN = new int[]{SLOT_RESULT};
    private static final int[] SLOTS_FOR_SIDES = new int[]{SLOT_INPUT};

    private static final Component DEFAULT_NAME = Component.translatable("container.freshcaa.sun_drying_table");

    private NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private int progress;
    private boolean inSun;
    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.SMELTING);

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int dataId) {
            return switch (dataId) {
                case DATA_PROGRESS -> SunDryingTableBlockEntity.this.progress;
                case DATA_TOTAL -> DRY_TIME;
                case DATA_IN_SUN -> SunDryingTableBlockEntity.this.inSun ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int dataId, int value) {
            switch (dataId) {
                case DATA_PROGRESS -> SunDryingTableBlockEntity.this.progress = value;
                case DATA_IN_SUN -> SunDryingTableBlockEntity.this.inSun = value != 0;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public SunDryingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SUN_DRYING_TABLE.get(), pos, state);
    }

    /**
     * True only in direct sunlight: daytime, nothing between the table and the sky, full sky light,
     * and no rain falling on it.
     */
    public static boolean isInSun(Level level, BlockPos pos) {
        BlockPos above = pos.above();
        return level.isBrightOutside()
                && level.canSeeSky(above)
                && level.getBrightness(LightLayer.SKY, above) >= MIN_SKY_LIGHT
                && !level.isRainingAt(above);
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, SunDryingTableBlockEntity table) {
        boolean wasInSun = state.getValue(SunDryingTableBlock.LIT);
        boolean nowInSun = isInSun(level, pos);
        boolean changed = false;

        ItemStack input = table.items.get(SLOT_INPUT);
        ItemStack result = ItemStack.EMPTY;
        if (nowInSun && !input.isEmpty() && input.is(ModTags.Items.SUN_DRYABLE)) {
            SingleRecipeInput recipeInput = new SingleRecipeInput(input);
            RecipeHolder<? extends AbstractCookingRecipe> recipe = table.quickCheck.getRecipeFor(recipeInput, level).orElse(null);
            if (recipe != null) {
                result = recipe.value().assemble(recipeInput);
            }
        }

        if (!result.isEmpty() && canOutput(table.items.get(SLOT_RESULT), result, table.getMaxStackSize())) {
            table.progress++;
            if (table.progress >= DRY_TIME) {
                table.progress = 0;
                ItemStack existing = table.items.get(SLOT_RESULT);
                if (existing.isEmpty()) {
                    table.items.set(SLOT_RESULT, result.copy());
                } else {
                    existing.grow(result.getCount());
                }
                input.shrink(1);
                changed = true;
            }
        } else if (table.progress > 0) {
            table.progress = Math.max(0, table.progress - 2);
        }

        table.inSun = nowInSun;
        if (wasInSun != nowInSun) {
            state = state.setValue(SunDryingTableBlock.LIT, nowInSun);
            level.setBlockAndUpdate(pos, state);
            changed = true;
        }

        if (changed) {
            setChanged(level, pos, state);
        }
    }

    private static boolean canOutput(ItemStack existing, ItemStack result, int maxStackSize) {
        if (existing.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(existing, result)) {
            return false;
        }
        int total = existing.getCount() + result.getCount();
        return total <= Math.min(maxStackSize, result.getMaxStackSize());
    }

    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new SunDryingTableMenu(containerId, inventory, this, this.dataAccess);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.progress = input.getIntOr("progress", 0);
        this.inSun = input.getBooleanOr("in_sun", false);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("progress", this.progress);
        output.putBoolean("in_sun", this.inSun);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        return slot == SLOT_INPUT;
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        if (direction == Direction.DOWN) {
            return SLOTS_FOR_DOWN;
        }
        return direction == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, @Nullable Direction direction) {
        return this.canPlaceItem(slot, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
        return slot == SLOT_RESULT;
    }
}
