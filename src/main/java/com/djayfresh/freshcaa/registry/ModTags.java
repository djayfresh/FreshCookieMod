package com.djayfresh.freshcaa.registry;

import com.djayfresh.freshcaa.FreshCookies;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModTags {
    public static final class Items {
        /** Items the Sun Drying Table is allowed to process (it uses their furnace recipe). */
        public static final TagKey<Item> SUN_DRYABLE = tag("sun_dryable");
        /** Items accepted by the Sun Drying Table's four upgrade slots (the lens). */
        public static final TagKey<Item> SUN_DRYING_TABLE_UPGRADES = tag("sun_drying_table_upgrades");

        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(FreshCookies.MODID, name));
        }

        private Items() {}
    }

    private ModTags() {}
}
