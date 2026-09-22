package com.djayfresh.freshcaa.worldgen;

import com.djayfresh.freshcaa.FreshCookies;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.Feature;

/** Sapling growers. The tree shapes themselves live in data/freshcaa/worldgen/feature. */
public final class ModTreeGrowers {
    public static final ResourceKey<Feature> PECAN_TREE = featureKey("pecan");
    public static final ResourceKey<Feature> MACADAMIA_TREE = featureKey("macadamia");

    public static final TreeGrower PECAN = new TreeGrower(FreshCookies.MODID + ":pecan",
            WeightedList.of(PECAN_TREE), WeightedList.of(), WeightedList.of(), PECAN_TREE);
    public static final TreeGrower MACADAMIA = new TreeGrower(FreshCookies.MODID + ":macadamia",
            WeightedList.of(MACADAMIA_TREE), WeightedList.of(), WeightedList.of(), MACADAMIA_TREE);

    private static ResourceKey<Feature> featureKey(String name) {
        return ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(FreshCookies.MODID, name));
    }

    private ModTreeGrowers() {}
}
