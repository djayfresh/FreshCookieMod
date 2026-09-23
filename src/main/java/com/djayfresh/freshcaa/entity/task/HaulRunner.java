package com.djayfresh.freshcaa.entity.task;

import com.djayfresh.freshcaa.entity.FactoryWorker;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

/**
 * Haul: carry items from the Supply block to the Output block, one item type per trip, honouring the filter slot.
 * Anything already in the worker's hands is delivered first, so a change of bindings never strands items.
 */
public final class HaulRunner implements TaskRunner {
    public static final HaulRunner INSTANCE = new HaulRunner();
    private static final int MAX_PER_TRIP = 64;

    private HaulRunner() {}

    @Override
    public @Nullable TaskStep nextStep(FactoryWorker worker) {
        WorkerTask task = worker.getTask();
        Level level = worker.level();
        BlockPos supply = task.binding(BindingSlot.SUPPLY).orElse(null);
        BlockPos output = task.binding(BindingSlot.OUTPUT).orElse(null);
        if (supply == null) {
            worker.setStatus(TaskStatus.MISSING_BINDING, Component.translatable(BindingSlot.SUPPLY.translationKey()));
            return null;
        }
        if (output == null) {
            worker.setStatus(TaskStatus.MISSING_BINDING, Component.translatable(BindingSlot.OUTPUT.translationKey()));
            return null;
        }

        if (!worker.getCarry().isEmpty()) {
            if (!WorkerTransfers.isContainer(level, output)) {
                worker.setStatus(TaskStatus.BLOCKED, FactoryWorker.describe(level, output));
                return null;
            }
            if (!carriesAnythingWanted(worker, level, output)) {
                // Nothing in hand is any use at the target (bindings or filter changed): take it back.
                return new TaskStep(supply, w -> {
                    WorkerTransfers.deliver(level, supply, w.getCarry());
                    if (!w.getCarry().isEmpty()) {
                        w.setStatus(TaskStatus.OUTPUT_FULL, FactoryWorker.describe(level, supply));
                    }
                });
            }
            return new TaskStep(output, w -> {
                WorkerTransfers.deliver(level, output, w.getCarry());
                if (w.getCarry().isEmpty()) {
                    w.setStatus(TaskStatus.WORKING, FactoryWorker.describe(level, supply));
                } else {
                    w.setStatus(TaskStatus.OUTPUT_FULL, FactoryWorker.describe(level, output));
                }
            });
        }

        ResourceHandler<ItemResource> from = WorkerTransfers.extractHandler(level, supply);
        if (from == null) {
            worker.setStatus(TaskStatus.BLOCKED, FactoryWorker.describe(level, supply));
            return null;
        }
        if (!WorkerTransfers.isContainer(level, output)) {
            worker.setStatus(TaskStatus.BLOCKED, FactoryWorker.describe(level, output));
            return null;
        }
        // Only pick up what the filter allows AND the target has a use for, so junk never leaves the chest.
        Predicate<ItemResource> filter = WorkerTransfers.filterFor(worker.getFilter().getItem(0))
                .and(resource -> WorkerTransfers.wants(level, output, resource.toStack(1)));
        if (!WorkerTransfers.hasExtractable(from, filter)) {
            worker.setStatus(TaskStatus.WAITING_SUPPLY, FactoryWorker.describe(level, supply));
            return null;
        }
        return new TaskStep(supply, w -> {
            int moved = WorkerTransfers.pickUp(from, w.getCarry(), filter, MAX_PER_TRIP);
            if (moved > 0) {
                w.setStatus(TaskStatus.WORKING, FactoryWorker.describe(level, output));
            } else {
                w.setStatus(TaskStatus.WAITING_SUPPLY, FactoryWorker.describe(level, supply));
            }
        });
    }

    private static boolean carriesAnythingWanted(FactoryWorker worker, Level level, BlockPos output) {
        for (int slot = 0; slot < worker.getCarry().getContainerSize(); slot++) {
            if (WorkerTransfers.wants(level, output, worker.getCarry().getItem(slot))) {
                return true;
            }
        }
        return false;
    }
}
