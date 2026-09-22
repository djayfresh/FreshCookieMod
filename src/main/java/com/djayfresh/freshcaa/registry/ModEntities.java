package com.djayfresh.freshcaa.registry;

import com.djayfresh.freshcaa.FreshCookies;
import com.djayfresh.freshcaa.entity.FactoryWorker;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(FreshCookies.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<FactoryWorker>> FACTORY_WORKER = ENTITIES.registerEntityType("factory_worker",
            FactoryWorker::new, MobCategory.CREATURE,
            builder -> builder.sized(0.6F, 1.95F).eyeHeight(1.62F).clientTrackingRange(10));

    public static void onAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(FACTORY_WORKER.get(), FactoryWorker.createAttributes().build());
    }

    public static void onSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(FACTORY_WORKER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private ModEntities() {}
}
