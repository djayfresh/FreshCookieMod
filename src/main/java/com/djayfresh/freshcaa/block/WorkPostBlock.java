package com.djayfresh.freshcaa.block;

import com.djayfresh.freshcaa.entity.FactoryWorker;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/**
 * The Work Post: a sign on a post that marks a worker's home. Placing one next to hired workers that have no home
 * gives them this one. Buildings (Phase 5c) rise from the post, facing the way it faces.
 */
public class WorkPostBlock extends HorizontalDirectionalBlock {
    /** Workers this close when the post is placed adopt it, if they have no home yet. */
    private static final double ADOPT_RANGE = 6.0;

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(6, 0, 6, 10, 11, 10),
            Block.box(1, 9, 5, 15, 16, 11));

    public WorkPostBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);
        if (level.isClientSide()) {
            return;
        }
        List<FactoryWorker> nearby = level.getEntitiesOfClass(FactoryWorker.class, new AABB(pos).inflate(ADOPT_RANGE),
                worker -> worker.isHired() && worker.getHome() == null);
        for (FactoryWorker worker : nearby) {
            worker.setHome(pos);
        }
        if (!nearby.isEmpty() && placer instanceof Player player) {
            player.sendOverlayMessage(Component.translatable("freshcaa.work_post.adopted", nearby.size()));
        }
    }
}
