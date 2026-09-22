package com.djayfresh.freshcaa.registry;

import com.djayfresh.freshcaa.FreshCookies;
import com.djayfresh.freshcaa.menu.SunDryingTableMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, FreshCookies.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<SunDryingTableMenu>> SUN_DRYING_TABLE = MENUS.register("sun_drying_table",
            () -> new MenuType<>(SunDryingTableMenu::new, FeatureFlags.DEFAULT_FLAGS));

    private ModMenus() {}
}
