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

/** Menu for the Sun Drying Table: input slot, result slot, player inventory. Slot layout matches the furnace GUI. */
public class SunDryingTableMenu extends AbstractContainerMenu {
    private static final int TABLE_SLOTS = SunDryingTableBlockEntity.SLOT_COUNT;
    private static final int INV_START = TABLE_SLOTS;
    private static final int INV_END = INV_START + 27;
    private static final int HOTBAR_END = INV_END + 9;

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
        this.addStandardInventorySlots(inventory, 8, 84);
        this.addDataSlots(data);
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
            } else if (slotIndex == SunDryingTableBlockEntity.SLOT_INPUT) {
                if (!this.moveItemStackTo(stack, INV_START, HOTBAR_END, false)) {
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
