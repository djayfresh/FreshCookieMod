package com.djayfresh.freshcaa.client;

import com.djayfresh.freshcaa.FreshCookies;
import com.djayfresh.freshcaa.registry.ModBlocks;
import com.djayfresh.freshcaa.registry.ModEntities;
import com.djayfresh.freshcaa.registry.ModMenus;
import java.util.List;
import net.minecraft.client.color.block.BlockTintSources;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/** Client-only setup: renderers, screens, and leaf colours. Never loaded on a dedicated server. */
@Mod(value = FreshCookies.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = FreshCookies.MODID, value = Dist.CLIENT)
public class FreshCookiesClient {
    /** Fixed foliage colours carried over from the 1.6.4 ColorizerLeaves (ARGB). */
    public static final int PECAN_LEAF_COLOR = 0xFF00472E;
    public static final int MACADAMIA_LEAF_COLOR = 0xFF28622E;

    public FreshCookiesClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FACTORY_WORKER.get(), FactoryWorkerRenderer::new);
    }

    @SubscribeEvent
    static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.SUN_DRYING_TABLE.get(), SunDryingTableScreen::new);
    }

    @SubscribeEvent
    static void onBlockColors(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(List.of(BlockTintSources.constant(PECAN_LEAF_COLOR)), ModBlocks.PECAN_LEAVES.get());
        event.register(List.of(BlockTintSources.constant(MACADAMIA_LEAF_COLOR)), ModBlocks.MACADAMIA_LEAVES.get());
    }
}
