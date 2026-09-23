package com.djayfresh.freshcaa.entity.ai;

import com.djayfresh.freshcaa.entity.FactoryWorker;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/** A hired worker with nothing to do drifts around its Work Post (or where it was hired) instead of wandering off. */
public class WanderNearHomeGoal extends Goal {
    private static final int START_CHANCE = 120;
    private static final int RADIUS = 5;
    private static final double SPEED = 0.8;

    private final FactoryWorker worker;
    private @Nullable Vec3 target;

    public WanderNearHomeGoal(FactoryWorker worker) {
        this.worker = worker;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.worker.isHired() || this.worker.getRandom().nextInt(reducedTickDelay(START_CHANCE)) != 0) {
            return false;
        }
        BlockPos home = this.worker.getHomeOrPosition();
        double x = home.getX() + 0.5 + (this.worker.getRandom().nextDouble() - 0.5) * 2 * RADIUS;
        double z = home.getZ() + 0.5 + (this.worker.getRandom().nextDouble() - 0.5) * 2 * RADIUS;
        this.target = new Vec3(x, home.getY(), z);
        return true;
    }

    @Override
    public void start() {
        if (this.target != null) {
            this.worker.getNavigation().moveTo(this.target.x, this.target.y, this.target.z, SPEED);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && !this.worker.getNavigation().isDone();
    }

    @Override
    public void stop() {
        this.target = null;
        this.worker.getNavigation().stop();
    }
}
