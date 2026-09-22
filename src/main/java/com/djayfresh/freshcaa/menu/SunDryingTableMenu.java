package com.djayfresh.freshcaa.menu;

import com.djayfresh.freshcaa.block.entity.SunDryingTableBlockEntity;
import com.djayfresh.freshcaa.registry.ModMenus;
import com.djayfresh.freshcaa.registry.ModTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Menu for the Sun Drying Table: input slot, result slot, a 2x2 block of upgrade (lens) slots, player inventory.
 * The input/result layout matches the furnace GUI; upgrades sit to the right of the result slot.
 */
public class SunDryingTableMenu extends AbstractContainerMenu {
    private static final int TABLE_SLOTS = SunDryingTableBlockEntity.SLOT_COUNT;
    private static final int UPGRADE_START = SunDryingTableBlockEntity.SLOT_UPGRADE_START;
    private static final int UPGRADE_END = TABLE_SLOTS;
    private static final int INV_START = TABLE_SLOTS;
    private static final int INV_END = INV_START + 27;
    private static final int HOTBAR_END = INV_END + 9;

    /** Screen positions of the four upgrade slots, in slot order: a column on the right, clear of the result frame. */
    public static final int[][] UPGRADE_SLOT_POSITIONS = {{152, 10}, {152, 28}, {152, 46}, {152, 64}};

    private final Container container;
    private final ContainerData data;

    /** Client-side constructor. */
    public SunDryingTableMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(TABLE_SLOTS), new SimpleContainerData(SunDryingTableBlockEntity.DATA_COUNT));
    }

    public SunDryingTableMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(ModMenus.SUN_DRYING_TABLE.get(), containerId);
        checkContainerSize(container, TABLE_SLOTS);
        checkContainerDataCount(data, SunDryingTableBlockEntity.DATA_COUNT);
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, SunDryingTableBlockEntity.SLOT_INPUT, 56, 17));
        this.addSlot(new Slot(container, SunDryingTableBlockEntity.SLOT_RESULT, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }
        });
        for (int i = 0; i < SunDryingTableBlockEntity.UPGRADE_SLOT_COUNT; i++) {
            this.addSlot(new UpgradeSlot(container, UPGRADE_START + i, UPGRADE_SLOT_POSITIONS[i][0], UPGRADE_SLOT_POSITIONS[i][1]));
        }
        this.addStandardInventorySlots(inventory, 8, 84);
        this.addDataSlots(data);
    }

    /** One lens (or future upgrade) per slot. */
    private static class UpgradeSlot extends Slot {
        UpgradeSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.is(ModTags.Items.SUN_DRYING_TABLE_UPGRADES);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    /** 0..1 fraction of the current drying cycle. */
    public float getProgress() {
        int total = this.data.get(SunDryingTableBlockEntity.DATA_TOTAL);
        int progress = this.data.get(SunDryingTableBlockEntity.DATA_PROGRESS);
        return total == 0 ? 0.0F : Math.clamp((float) progress / total, 0.0F, 1.0F);
    }

    public boolean isInSun() {
        return this.data.get(SunDryingTableBlockEntity.DATA_IN_SUN) != 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            clicked = stack.copy();
            if (slotIndex == SunDryingTableBlockEntity.SLOT_RESULT) {
                if (!this.moveItemStackTo(stack, INV_START, HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, clicked);
            } else if (slotIndex < TABLE_SLOTS) {
                if (!this.moveItemStackTo(stack, INV_START, HOTBAR_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.is(ModTags.Items.SUN_DRYING_TABLE_UPGRADES)) {
                if (!this.moveItemStackTo(stack, UPGRADE_START, UPGRADE_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.is(ModTags.Items.SUN_DRYABLE)) {
                if (!this.moveItemStackTo(stack, SunDryingTableBlockEntity.SLOT_INPUT, SunDryingTableBlockEntity.SLOT_INPUT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex < INV_END) {
                if (!this.moveItemStackTo(stack, INV_END, HOTBAR_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, INV_START, INV_END, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == clicked.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        }
        return clicked;
    }
}
