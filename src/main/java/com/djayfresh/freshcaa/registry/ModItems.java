package com.djayfresh.freshcaa.registry;

import com.djayfresh.freshcaa.FreshCookies;
import com.djayfresh.freshcaa.item.CookieDoughItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FreshCookies.MODID);

    // Cookies. Nutrition values match the 1.6.4 defaults.
    public static final DeferredItem<Item> CHOCOLATE_CHIP_COOKIE = food("chocolate_chip_cookie", 4, 0.3F);
    public static final DeferredItem<Item> OATMEAL_RAISIN_COOKIE = food("oatmeal_raisin_cookie", 8, 0.3F);
    public static final DeferredItem<Item> PEANUT_BUTTER_COOKIE = food("peanut_butter_cookie", 8, 0.3F);
    public static final DeferredItem<Item> PECAN_COOKIE = food("pecan_cookie", 5, 0.3F);
    public static final DeferredItem<Item> WHITE_MACADAMIA_COOKIE = food("white_macadamia_cookie", 5, 0.3F);

    // Doughs. Edible, barely nourishing, and (by config) a poison risk.
    public static final DeferredItem<CookieDoughItem> COOKIE_DOUGH = dough("cookie_dough");
    public static final DeferredItem<CookieDoughItem> CHOCOLATE_CHIP_COOKIE_DOUGH = dough("chocolate_chip_cookie_dough");
    public static final DeferredItem<CookieDoughItem> OATMEAL_RAISIN_COOKIE_DOUGH = dough("oatmeal_raisin_cookie_dough");
    public static final DeferredItem<CookieDoughItem> PEANUT_BUTTER_COOKIE_DOUGH = dough("peanut_butter_cookie_dough");
    public static final DeferredItem<CookieDoughItem> PECAN_COOKIE_DOUGH = dough("pecan_cookie_dough");
    public static final DeferredItem<CookieDoughItem> WHITE_MACADAMIA_COOKIE_DOUGH = dough("white_macadamia_cookie_dough");

    // Ingredients
    public static final DeferredItem<Item> RAISIN = food("raisin", 1, 1.0F);
    public static final DeferredItem<Item> OATS = food("oats", 2, 1.0F);
    public static final DeferredItem<Item> PECAN = food("pecan", 1, 0.0F);
    public static final DeferredItem<Item> WHITE_MACADAMIA = food("white_macadamia", 1, 0.0F);

    // Seeds (plantable on farmland, like wheat seeds)
    public static final DeferredItem<BlockItem> PEANUTS = ITEMS.registerItem("peanuts", p -> new BlockItem(ModBlocks.PEANUT_PLANT.get(), p.useItemDescriptionPrefix()));
    public static final DeferredItem<BlockItem> GRAPES = ITEMS.registerItem("grapes", p -> new BlockItem(ModBlocks.GRAPE_VINE.get(), p.useItemDescriptionPrefix()));

    // Block items
    public static final DeferredItem<BlockItem> SUN_DRYING_TABLE = ITEMS.registerSimpleBlockItem(ModBlocks.SUN_DRYING_TABLE);
    public static final DeferredItem<BlockItem> PECAN_LOG = ITEMS.registerSimpleBlockItem(ModBlocks.PECAN_LOG);
    public static final DeferredItem<BlockItem> PECAN_PLANKS = ITEMS.registerSimpleBlockItem(ModBlocks.PECAN_PLANKS);
    public static final DeferredItem<BlockItem> PECAN_LEAVES = ITEMS.registerSimpleBlockItem(ModBlocks.PECAN_LEAVES);
    public static final DeferredItem<BlockItem> PECAN_SAPLING = ITEMS.registerSimpleBlockItem(ModBlocks.PECAN_SAPLING);
    public static final DeferredItem<BlockItem> MACADAMIA_LOG = ITEMS.registerSimpleBlockItem(ModBlocks.MACADAMIA_LOG);
    public static final DeferredItem<BlockItem> MACADAMIA_PLANKS = ITEMS.registerSimpleBlockItem(ModBlocks.MACADAMIA_PLANKS);
    public static final DeferredItem<BlockItem> MACADAMIA_LEAVES = ITEMS.registerSimpleBlockItem(ModBlocks.MACADAMIA_LEAVES);
    public static final DeferredItem<BlockItem> MACADAMIA_SAPLING = ITEMS.registerSimpleBlockItem(ModBlocks.MACADAMIA_SAPLING);

    // Entities register before items, so the entity type is available here.
    public static final DeferredItem<SpawnEggItem> FACTORY_WORKER_SPAWN_EGG = ITEMS.registerItem("factory_worker_spawn_egg", SpawnEggItem::new,
            p -> p.spawnEgg(ModEntities.FACTORY_WORKER.get()));

    private static DeferredItem<Item> food(String name, int nutrition, float saturationModifier) {
        return ITEMS.registerSimpleItem(name, p -> p.food(new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturationModifier).build()));
    }

    private static DeferredItem<CookieDoughItem> dough(String name) {
        return ITEMS.registerItem(name, CookieDoughItem::new, p -> p.food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.0F).build()));
    }

    private ModItems() {}
}
