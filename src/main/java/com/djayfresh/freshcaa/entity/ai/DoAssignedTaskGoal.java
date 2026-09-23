package com.djayfresh.freshcaa.entity.ai;

import com.djayfresh.freshcaa.Config;
import com.djayfresh.freshcaa.entity.FactoryWorker;
import com.djayfresh.freshcaa.entity.task.TaskRunner;
import com.djayfresh.freshcaa.entity.task.TaskStatus;
import com.djayfresh.freshcaa.entity.task.TaskStep;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Runs a hired worker's task as a loop of PLAN -> WALK -> WORK. The task's runner supplies each step; this goal only
 * handles walking there, the working animation, giving up on unreachable targets, and pacing.
 */
public class DoAssignedTaskGoal extends Goal {
    /** How close (blocks, squared) the worker must get to the target block's centre before it starts working. */
    private static final double REACH_SQR = 3.0 * 3.0;
    private static final int WALK_TIMEOUT = 300;
    private static final int MAX_FAILURES = 3;
    private static final int RETRY_AFTER_BLOCKED = 200;
    private static final int IDLE_PAUSE = 40;
    private static final int SWING_INTERVAL = 8;
    private static final double SPEED = 1.0;

    private enum Phase { PLAN, WALK, WORK }

    private final FactoryWorker worker;
    private Phase phase = Phase.PLAN;
    private @Nullable TaskStep step;
    private int timer;
    private int failures;
    private int cooldown;

    public DoAssignedTaskGoal(FactoryWorker worker) {
        this.worker = worker;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    private boolean hasWork() {
        if (!this.worker.isHired() || !this.worker.getTask().type().isImplemented() || this.worker.getTask().type().bindings().isEmpty()) {
            return false;
        }
        return !Config.WORKER_RESTS_AT_NIGHT.get() || this.worker.level().isBrightOutside();
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }
        return this.hasWork();
    }

    @Override
    public boolean canContinueToUse() {
        return this.cooldown == 0 && this.hasWork();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.phase = Phase.PLAN;
        this.step = null;
        this.failures = 0;
    }

    @Override
    public void stop() {
        this.worker.getNavigation().stop();
        this.step = null;
        this.phase = Phase.PLAN;
        if (this.worker.getStatus() == TaskStatus.WORKING) {
            this.worker.setStatus(TaskStatus.IDLE, null);
        }
    }

    @Override
    public void tick() {
        switch (this.phase) {
            case PLAN -> this.plan();
            case WALK -> this.walk();
            case WORK -> this.work();
        }
    }

    private void plan() {
        TaskRunner runner = TaskRunner.forType(this.worker.getTask().type());
        this.step = runner.nextStep(this.worker);
        if (this.step == null) {
            this.cooldown = IDLE_PAUSE;
            return;
        }
        this.timer = WALK_TIMEOUT;
        this.phase = Phase.WALK;
        this.moveToStep();
    }

    private void moveToStep() {
        BlockPos target = this.step.target();
        this.worker.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, 1, SPEED);
    }

    private void walk() {
        if (this.step == null) {
            this.phase = Phase.PLAN;
            return;
        }
        Vec3 centre = Vec3.atCenterOf(this.step.target());
        this.worker.getLookControl().setLookAt(centre);
        if (this.worker.position().distanceToSqr(centre) <= REACH_SQR) {
            this.worker.getNavigation().stop();
            this.phase = Phase.WORK;
            this.timer = Config.WORKER_WORK_TICKS.get();
            return;
        }
        this.timer--;
        if (this.worker.getNavigation().isDone() || this.timer <= 0) {
            this.failures++;
            if (this.failures >= MAX_FAILURES) {
                this.worker.setStatus(TaskStatus.BLOCKED, FactoryWorker.describe(this.worker.level(), this.step.target()));
                this.failures = 0;
                this.cooldown = RETRY_AFTER_BLOCKED;
                this.worker.showUnhappy();
                return;
            }
            this.timer = WALK_TIMEOUT;
            this.moveToStep();
        }
    }

    private void work() {
        if (this.step == null) {
            this.phase = Phase.PLAN;
            return;
        }
        this.worker.getLookControl().setLookAt(Vec3.atCenterOf(this.step.target()));
        if (this.timer % SWING_INTERVAL == 0) {
            this.worker.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT);
        }
        this.timer--;
        if (this.timer <= 0) {
            this.step.action().accept(this.worker);
            this.failures = 0;
            this.step = null;
            this.phase = Phase.PLAN;
        }
    }
}
