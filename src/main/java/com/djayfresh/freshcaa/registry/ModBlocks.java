package com.djayfresh.freshcaa.registry;

import com.djayfresh.freshcaa.FreshCookies;
import com.djayfresh.freshcaa.block.GrapeVineBlock;
import com.djayfresh.freshcaa.block.PeanutPlantBlock;
import com.djayfresh.freshcaa.block.SunDryingTableBlock;
import com.djayfresh.freshcaa.worldgen.ModTreeGrowers;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FreshCookies.MODID);

    // Sun Drying Table: one block with a LIT state instead of the old active/idle block pair.
    public static final DeferredBlock<SunDryingTableBlock> SUN_DRYING_TABLE = BLOCKS.registerBlock("sun_drying_table", SunDryingTableBlock::new,
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(SunDryingTableBlock.LIT) ? 6 : 0));

    // Crops
    public static final DeferredBlock<PeanutPlantBlock> PEANUT_PLANT = BLOCKS.registerBlock("peanut_plant", PeanutPlantBlock::new, ModBlocks::cropProperties);
    public static final DeferredBlock<GrapeVineBlock> GRAPE_VINE = BLOCKS.registerBlock("grape_vine", GrapeVineBlock::new, ModBlocks::cropProperties);

    // Pecan tree
    public static final DeferredBlock<RotatedPillarBlock> PECAN_LOG = BLOCKS.registerBlock("pecan_log", RotatedPillarBlock::new, () -> logProperties(MapColor.WOOD, MapColor.PODZOL));
    public static final DeferredBlock<Block> PECAN_PLANKS = BLOCKS.registerSimpleBlock("pecan_planks", () -> planksProperties(MapColor.WOOD));
    public static final DeferredBlock<TintedParticleLeavesBlock> PECAN_LEAVES = BLOCKS.registerBlock("pecan_leaves", p -> new TintedParticleLeavesBlock(0.01F, p), ModBlocks::leavesProperties);
    public static final DeferredBlock<SaplingBlock> PECAN_SAPLING = BLOCKS.registerBlock("pecan_sapling", p -> new SaplingBlock(ModTreeGrowers.PECAN, p), ModBlocks::saplingProperties);

    // Macadamia tree
    public static final DeferredBlock<RotatedPillarBlock> MACADAMIA_LOG = BLOCKS.registerBlock("macadamia_log", RotatedPillarBlock::new, () -> logProperties(MapColor.COLOR_BROWN, MapColor.TERRACOTTA_BROWN));
    public static final DeferredBlock<Block> MACADAMIA_PLANKS = BLOCKS.registerSimpleBlock("macadamia_planks", () -> planksProperties(MapColor.COLOR_BROWN));
    public static final DeferredBlock<TintedParticleLeavesBlock> MACADAMIA_LEAVES = BLOCKS.registerBlock("macadamia_leaves", p -> new TintedParticleLeavesBlock(0.01F, p), ModBlocks::leavesProperties);
    public static final DeferredBlock<SaplingBlock> MACADAMIA_SAPLING = BLOCKS.registerBlock("macadamia_sapling", p -> new SaplingBlock(ModTreeGrowers.MACADAMIA, p), ModBlocks::saplingProperties);

    private static BlockBehaviour.Properties cropProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollision()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
                .pushReaction(PushReaction.POPPED);
    }

    private static BlockBehaviour.Properties logProperties(MapColor topColor, MapColor sideColor) {
        return BlockBehaviour.Properties.of()
                .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? topColor : sideColor)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava();
    }

    private static BlockBehaviour.Properties planksProperties(MapColor color) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava();
    }

    private static BlockBehaviour.Properties leavesProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .strength(0.2F)
                .randomTicks()
                .sound(SoundType.GRASS)
                .noOcclusion()
                .isValidSpawn(Blocks::ocelotOrParrot)
                .isSuffocating((state, level, pos) -> false)
                .ignitedByLava()
                .pushReaction(PushReaction.POPPED)
                .isRedstoneConductor((state, level, pos) -> false);
    }

    private static BlockBehaviour.Properties saplingProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollision()
                .randomTicks()
                .instabreak()
                .sound(SoundType.GRASS)
                .pushReaction(PushReaction.POPPED);
    }

    private ModBlocks() {}
}
