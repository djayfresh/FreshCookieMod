package com.djayfresh.freshcaa.entity.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/** During daytime, pick a random point within range and walk there. Successor of the old "circles" task. */
public class WanderInSunGoal extends Goal {
    private static final int START_CHANCE = 60;

    private final PathfinderMob mob;
    private final double speedModifier;
    private final float range;
    private @Nullable Vec3 target;

    public WanderInSunGoal(PathfinderMob mob, double speedModifier, float range) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.range = range;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.mob.level().isBrightOutside()) {
            return false;
        }
        if (this.mob.getRandom().nextInt(reducedTickDelay(START_CHANCE)) != 0) {
            return false;
        }
        double x = this.mob.getX() + (this.mob.getRandom().nextDouble() - 0.5) * this.range;
        double z = this.mob.getZ() + (this.mob.getRandom().nextDouble() - 0.5) * this.range;
        this.target = new Vec3(x, this.mob.getY(), z);
        return true;
    }

    @Override
    public void start() {
        if (this.target != null) {
            this.mob.getNavigation().moveTo(this.target.x, this.target.y, this.target.z, this.speedModifier);
            this.mob.getLookControl().setLookAt(this.target.add(0.0, 2.0, 0.0));
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && !this.mob.getNavigation().isDone();
    }

    @Override
    public void stop() {
        this.target = null;
        this.mob.getNavigation().stop();
    }
}
