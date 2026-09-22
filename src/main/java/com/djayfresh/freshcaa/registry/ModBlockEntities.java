package com.djayfresh.freshcaa.registry;

import com.djayfresh.freshcaa.FreshCookies;
import com.djayfresh.freshcaa.block.entity.SunDryingTableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, FreshCookies.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SunDryingTableBlockEntity>> SUN_DRYING_TABLE = BLOCK_ENTITIES.register("sun_drying_table",
            () -> new BlockEntityType<>(SunDryingTableBlockEntity::new, ModBlocks.SUN_DRYING_TABLE.get()));

    private ModBlockEntities() {}
}
