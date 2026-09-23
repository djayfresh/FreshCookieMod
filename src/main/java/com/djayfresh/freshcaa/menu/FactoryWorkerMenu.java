package com.djayfresh.freshcaa.menu;

import com.djayfresh.freshcaa.entity.FactoryWorker;
import com.djayfresh.freshcaa.entity.task.BindingSlot;
import com.djayfresh.freshcaa.entity.task.TaskType;
import com.djayfresh.freshcaa.registry.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * The worker's task screen. Slots: the four carry slots, one filter slot, the player inventory. Everything else (task
 * choice, bindings, dismissal) goes through {@link #clickMenuButton} ids, and the client reads the result straight
 * from the worker's synched data.
 */
public class FactoryWorkerMenu extends AbstractContainerMenu {
    public static final int BUTTON_TASK_BASE = 0;
    public static final int BUTTON_NEAREST_BASE = 10;
    public static final int BUTTON_CLEAR_BASE = 20;
    public static final int BUTTON_DISMISS = 30;

    public static final int CARRY_X = 8;
    public static final int CARRY_Y = 108;
    public static final int FILTER_X = 152;
    public static final int FILTER_Y = 108;
    public static final int INVENTORY_Y = 140;

    private static final int CARRY_START = 0;
    private static final int FILTER_SLOT = FactoryWorker.CARRY_SLOTS;
    private static final int INV_START = FILTER_SLOT + 1;
    private static final int INV_END = INV_START + 27;
    private static final int HOTBAR_END = INV_END + 9;

    private final @Nullable FactoryWorker worker;
    private final Container carry;
    private final Container filter;

    /** Client-side constructor: the server wrote the worker's entity id after the menu type. */
    public FactoryWorkerMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, inventory, findWorker(inventory, extraData.readVarInt()));
    }

    public FactoryWorkerMenu(int containerId, Inventory inventory, @Nullable FactoryWorker worker) {
        super(ModMenus.FACTORY_WORKER.get(), containerId);
        this.worker = worker;
        this.carry = worker != null ? worker.getCarry() : new SimpleContainer(FactoryWorker.CARRY_SLOTS);
        this.filter = worker != null ? worker.getFilter() : new SimpleContainer(1);

        for (int i = 0; i < FactoryWorker.CARRY_SLOTS; i++) {
            this.addSlot(new Slot(this.carry, i, CARRY_X + i * 18, CARRY_Y));
        }
        this.addSlot(new Slot(this.filter, 0, FILTER_X, FILTER_Y) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        this.addStandardInventorySlots(inventory, 8, INVENTORY_Y);
    }

    private static @Nullable FactoryWorker findWorker(Inventory inventory, int entityId) {
        Entity entity = inventory.player.level().getEntity(entityId);
        return entity instanceof FactoryWorker worker ? worker : null;
    }

    public @Nullable FactoryWorker getWorker() {
        return this.worker;
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (this.worker == null || !this.worker.isHired()) {
            return false;
        }
        if (buttonId >= BUTTON_TASK_BASE && buttonId < BUTTON_NEAREST_BASE) {
            this.worker.setTaskType(TaskType.byOrdinal(buttonId - BUTTON_TASK_BASE));
            return true;
        }
        if (buttonId >= BUTTON_NEAREST_BASE && buttonId < BUTTON_CLEAR_BASE) {
            this.worker.bindNearest(BindingSlot.byOrdinal(buttonId - BUTTON_NEAREST_BASE));
            return true;
        }
        if (buttonId >= BUTTON_CLEAR_BASE && buttonId < BUTTON_DISMISS) {
            this.worker.unbind(BindingSlot.byOrdinal(buttonId - BUTTON_CLEAR_BASE));
            return true;
        }
        if (buttonId == BUTTON_DISMISS) {
            this.worker.dismiss(player);
            player.closeContainer();
            return true;
        }
        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.worker != null && this.worker.isAlive() && this.worker.isHired() && player.distanceToSqr(this.worker) < 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            clicked = stack.copy();
            if (slotIndex < INV_START) {
                if (!this.moveItemStackTo(stack, INV_START, HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, CARRY_START, FILTER_SLOT, false)) {
                if (slotIndex < INV_END) {
                    if (!this.moveItemStackTo(stack, INV_END, HOTBAR_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(stack, INV_START, INV_END, false)) {
                    return ItemStack.EMPTY;
                }
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
