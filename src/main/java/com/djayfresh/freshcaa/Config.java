package com.djayfresh.freshcaa;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Common config. Mirrors the gameplay toggles the 1.6.4 version exposed in freshcaa.cfg, plus the table upgrades. */
public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue COOKIE_DOUGH_POISONOUS = BUILDER
            .comment("Whether eating raw cookie dough has a chance to poison you")
            .define("cookieDoughPoisonous", true);

    public static final ModConfigSpec.DoubleValue COOKIE_DOUGH_POISON_CHANCE = BUILDER
            .comment("Chance (0.0 - 1.0) that raw cookie dough poisons you, when enabled")
            .defineInRange("cookieDoughPoisonChance", 0.4, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue LENS_SPEED_BONUS = BUILDER
            .comment("Extra drying speed per lens on a Sun Drying Table (1.0 = each lens doubles the base speed; 4 lenses = 5x)")
            .defineInRange("lensSpeedBonus", 1.0, 0.0, 4.0);

    public static final ModConfigSpec.IntValue WHEAT_SEED_DROP_WEIGHT = BUILDER
            .comment("Grass that would drop wheat seeds picks one of wheat seeds, peanuts or grapes by weight.",
                    "Weight of wheat seeds in that pick (vanilla's own weight is 10)")
            .defineInRange("wheatSeedDropWeight", 10, 0, 1000);

    public static final ModConfigSpec.IntValue PEANUT_DROP_WEIGHT = BUILDER
            .comment("Weight of peanuts in the grass seed pick (0 disables)")
            .defineInRange("peanutDropWeight", 10, 0, 1000);

    public static final ModConfigSpec.IntValue GRAPE_DROP_WEIGHT = BUILDER
            .comment("Weight of grapes in the grass seed pick (0 disables)")
            .defineInRange("grapeDropWeight", 10, 0, 1000);

    static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {}
}
