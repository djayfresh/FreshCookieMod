package com.djayfresh.freshcaa;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Common config. Mirrors the gameplay toggles the 1.6.4 version exposed in freshcaa.cfg. */
public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue COOKIE_DOUGH_POISONOUS = BUILDER
            .comment("Whether eating raw cookie dough has a chance to poison you")
            .define("cookieDoughPoisonous", true);

    public static final ModConfigSpec.DoubleValue COOKIE_DOUGH_POISON_CHANCE = BUILDER
            .comment("Chance (0.0 - 1.0) that raw cookie dough poisons you, when enabled")
            .defineInRange("cookieDoughPoisonChance", 0.4, 0.0, 1.0);

    static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {}
}
