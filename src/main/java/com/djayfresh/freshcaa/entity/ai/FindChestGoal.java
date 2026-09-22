package com.djayfresh.freshcaa.entity.ai;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/** During daytime, walk to the closest chest within range. Re-scans on a cooldown to keep the search cheap. */
public class FindChestGoal extends Goal {
    private static final int VERTICAL_RANGE = 2;
    private static final int SEARCH_COOLDOWN = 100;
    private static final int REST_AFTER_VISIT = 200;

    private final PathfinderMob mob;
    private final double speedModifier;
    private final int range;
    private @Nullable BlockPos target;
    private int cooldown;

    public FindChestGoal(PathfinderMob mob, double speedModifier, int range) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.range = range;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }
        if (!this.mob.level().isBrightOutside()) {
            return false;
        }
        this.target = this.findClosestChest();
        if (this.target == null) {
            this.cooldown = reducedTickDelay(SEARCH_COOLDOWN);
            return false;
        }
        return true;
    }

    private @Nullable BlockPos findClosestChest() {
        BlockPos origin = this.mob.blockPosition();
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-this.range, -VERTICAL_RANGE, -this.range), origin.offset(this.range, VERTICAL_RANGE, this.range))) {
            BlockState state = this.mob.level().getBlockState(pos);
            if (state.is(Blocks.CHEST) || state.is(Blocks.TRAPPED_CHEST) || state.is(Blocks.BARREL)) {
                double distance = pos.distSqr(origin);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = pos.immutable();
                }
            }
        }
        return best;
    }

    @Override
    public void start() {
        if (this.target != null) {
            this.mob.getNavigation().moveTo(this.target.getX() + 0.5, this.target.getY(), this.target.getZ() + 0.5, this.speedModifier);
            this.mob.getLookControl().setLookAt(Vec3.atCenterOf(this.target));
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && !this.mob.getNavigation().isDone();
    }

    @Override
    public void stop() {
        this.target = null;
        this.cooldown = reducedTickDelay(REST_AFTER_VISIT + this.mob.getRandom().nextInt(REST_AFTER_VISIT));
        this.mob.getNavigation().stop();
    }
}
