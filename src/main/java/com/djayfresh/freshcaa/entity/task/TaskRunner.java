package com.djayfresh.freshcaa.entity.task;

import com.djayfresh.freshcaa.entity.FactoryWorker;
import org.jspecify.annotations.Nullable;

/**
 * Decides what a worker does next for its current task. Implementations set the worker's status while planning and
 * return null when there is nothing to do right now (the goal then waits a moment and asks again).
 */
public interface TaskRunner {
    TaskRunner NOTHING = worker -> null;

    @Nullable TaskStep nextStep(FactoryWorker worker);

    static TaskRunner forType(TaskType type) {
        return switch (type) {
            case HAUL -> HaulRunner.INSTANCE;
            default -> NOTHING;
        };
    }
}
