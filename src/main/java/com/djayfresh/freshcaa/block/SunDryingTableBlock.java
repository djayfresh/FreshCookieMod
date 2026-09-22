package com.djayfresh.freshcaa.block;

import com.djayfresh.freshcaa.block.entity.SunDryingTableBlockEntity;
import com.djayfresh.freshcaa.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/**
 * The Sun Drying Table: a four-legged table with a mesh top that needs no fuel, only the open daytime sky.
 * <ul>
 *   <li>{@code lit} - the table is currently in direct sunlight.</li>
 *   <li>{@code contents} - what is visible on the mesh: nothing, grapes, or raisins.</li>
 *   <li>{@code lens_1..4} - which upgrade slots hold a lens; each shows a lens model over one side.</li>
 * </ul>
 * All of these are derived from the block entity every tick, so the model always matches the inventory.
 */
public class SunDryingTableBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final EnumProperty<Contents> CONTENTS = EnumProperty.create("contents", Contents.class);
    public static final BooleanProperty LENS_1 = BooleanProperty.create("lens_1");
    public static final BooleanProperty LENS_2 = BooleanProperty.create("lens_2");
    public static final BooleanProperty LENS_3 = BooleanProperty.create("lens_3");
    public static final BooleanProperty LENS_4 = BooleanProperty.create("lens_4");
    public static final BooleanProperty[] LENSES = {LENS_1, LENS_2, LENS_3, LENS_4};

    /** Legs, rails and mesh from the Blockbench model (tools/models/sun_drying_table.bbmodel). Lenses are visual only. */
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(1, 0, 1, 3, 11, 3),
            Block.box(13, 0, 1, 15, 11, 3),
            Block.box(1, 0, 13, 3, 11, 15),
            Block.box(13, 0, 13, 15, 11, 15),
            Block.box(1, 11, 1, 15, 13, 15));

    public enum Contents implements StringRepresentable {
        EMPTY("empty"),
        GRAPES("grapes"),
        RAISINS("raisins");

        private final String name;

        Contents(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public SunDryingTableBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false)
                .setValue(CONTENTS, Contents.EMPTY)
                .setValue(LENS_1, false)
                .setValue(LENS_2, false)
                .setValue(LENS_3, false)
                .setValue(LENS_4, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.phys.BlockHitResult hitResult) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof SunDryingTableBlockEntity table) {
            player.openMenu(table);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SunDryingTableBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level instanceof ServerLevel serverLevel) {
            return createTickerHelper(type, ModBlockEntities.SUN_DRYING_TABLE.get(),
                    (innerLevel, pos, innerState, table) -> SunDryingTableBlockEntity.serverTick(serverLevel, pos, innerState, table));
        }
        return null;
    }

    /** Sparkles where the lenses focus the light, while the sun is on the table. */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT) || lensCount(state) == 0 || random.nextInt(3) != 0) {
            return;
        }
        double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
        double y = pos.getY() + 0.85 + random.nextDouble() * 0.15;
        double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
        level.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0, 0.01, 0.0);
    }

    public static int lensCount(BlockState state) {
        int count = 0;
        for (BooleanProperty lens : LENSES) {
            if (state.getValue(lens)) {
                count++;
            }
        }
        return count;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        Containers.updateNeighboursAfterDestroy(state, level, pos);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, CONTENTS, LENS_1, LENS_2, LENS_3, LENS_4);
    }
}
