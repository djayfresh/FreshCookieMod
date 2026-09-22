package com.djayfresh.freshcaa;

import com.djayfresh.freshcaa.registry.ModBlockEntities;
import com.djayfresh.freshcaa.registry.ModBlocks;
import com.djayfresh.freshcaa.registry.ModEntities;
import com.djayfresh.freshcaa.registry.ModItems;
import com.djayfresh.freshcaa.registry.ModMenus;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

/**
 * Fresh Cookies (FreshCAA) for NeoForge.
 * Entry point: wires every deferred register onto the mod event bus and defines the creative tab.
 */
@Mod(FreshCookies.MODID)
public class FreshCookies {
    public static final String MODID = "freshcaa";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> COOKIE_TAB = CREATIVE_MODE_TABS.register("cookies", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MODID))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> ModItems.CHOCOLATE_CHIP_COOKIE.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.CHOCOLATE_CHIP_COOKIE.get());
                output.accept(ModItems.OATMEAL_RAISIN_COOKIE.get());
                output.accept(ModItems.PEANUT_BUTTER_COOKIE.get());
                output.accept(ModItems.PECAN_COOKIE.get());
                output.accept(ModItems.WHITE_MACADAMIA_COOKIE.get());

                output.accept(ModItems.COOKIE_DOUGH.get());
                output.accept(ModItems.CHOCOLATE_CHIP_COOKIE_DOUGH.get());
                output.accept(ModItems.OATMEAL_RAISIN_COOKIE_DOUGH.get());
                output.accept(ModItems.PEANUT_BUTTER_COOKIE_DOUGH.get());
                output.accept(ModItems.PECAN_COOKIE_DOUGH.get());
                output.accept(ModItems.WHITE_MACADAMIA_COOKIE_DOUGH.get());

                output.accept(ModItems.RAISIN.get());
                output.accept(ModItems.OATS.get());
                output.accept(ModItems.PECAN.get());
                output.accept(ModItems.WHITE_MACADAMIA.get());
                output.accept(ModItems.PEANUTS.get());
                output.accept(ModItems.GRAPES.get());

                output.accept(ModItems.SUN_DRYING_TABLE.get());
                output.accept(ModItems.LENS.get());

                output.accept(ModItems.PECAN_SAPLING.get());
                output.accept(ModItems.PECAN_LOG.get());
                output.accept(ModItems.PECAN_PLANKS.get());
                output.accept(ModItems.PECAN_LEAVES.get());
                output.accept(ModItems.MACADAMIA_SAPLING.get());
                output.accept(ModItems.MACADAMIA_LOG.get());
                output.accept(ModItems.MACADAMIA_PLANKS.get());
                output.accept(ModItems.MACADAMIA_LEAVES.get());

                output.accept(ModItems.FACTORY_WORKER_SPAWN_EGG.get());
            })
            .build());

    public FreshCookies(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(ModEntities::onAttributeCreation);
        modEventBus.addListener(ModEntities::onSpawnPlacements);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
