package com.djayfresh.freshcaa.entity.task;

import com.djayfresh.freshcaa.entity.FactoryWorker;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;

/** One leg of a job: walk to {@code target}, work there for a moment, then run {@code action}. */
public record TaskStep(BlockPos target, Consumer<FactoryWorker> action) {
}
