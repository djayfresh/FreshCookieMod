package com.djayfresh.freshcaa.registry;

import com.djayfresh.freshcaa.FreshCookies;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModTags {
    public static final class Items {
        /** Items the Sun Drying Table is allowed to process (it uses their furnace recipe). */
        public static final TagKey<Item> SUN_DRYABLE = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(FreshCookies.MODID, "sun_dryable"));

        private Items() {}
    }

    private ModTags() {}
}
