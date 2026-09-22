package com.djayfresh.freshcaa.registry;

import com.djayfresh.freshcaa.FreshCookies;
import com.djayfresh.freshcaa.loot.GrassSeedsModifier;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Global loot modifier serializers. The modifiers themselves are data: data/freshcaa/loot_modifiers/*.json. */
public final class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, FreshCookies.MODID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<GrassSeedsModifier>> GRASS_SEEDS =
            LOOT_MODIFIERS.register("grass_seeds", () -> GrassSeedsModifier.CODEC);

    private ModLootModifiers() {}
}
