package com.djayfresh.freshcaa.loot;

import com.djayfresh.freshcaa.Config;
import com.djayfresh.freshcaa.registry.ModItems;
import com.djayfresh.freshcaa.registry.ModLootModifiers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

/**
 * Brings back the 1.6.4 grass seed pool. Whenever grass would drop wheat seeds, one weighted pick decides
 * whether the drop stays wheat seeds or becomes peanuts or grapes instead. The weights come from the config,
 * so the drop rate of grass itself is untouched; only what comes out changes.
 */
public class GrassSeedsModifier extends LootModifier {
    public static final MapCodec<GrassSeedsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> codecStart(instance)
            .apply(instance, GrassSeedsModifier::new));

    public GrassSeedsModifier(Optional<Holder<LootItemCondition>> condition, int priority) {
        super(condition, priority);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        int wheat = Math.max(0, Config.WHEAT_SEED_DROP_WEIGHT.get());
        int peanuts = Math.max(0, Config.PEANUT_DROP_WEIGHT.get());
        int grapes = Math.max(0, Config.GRAPE_DROP_WEIGHT.get());
        int total = wheat + peanuts + grapes;
        if (peanuts + grapes == 0) {
            return generatedLoot;
        }
        RandomSource random = context.getRandom();
        for (int i = 0; i < generatedLoot.size(); i++) {
            ItemStack stack = generatedLoot.get(i);
            if (!stack.is(Items.WHEAT_SEEDS)) {
                continue;
            }
            int roll = random.nextInt(total);
            if (roll < wheat) {
                continue;
            }
            ItemStack replacement = new ItemStack(roll < wheat + peanuts ? ModItems.PEANUTS.get() : ModItems.GRAPES.get(), stack.getCount());
            generatedLoot.set(i, replacement);
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return ModLootModifiers.GRASS_SEEDS.get();
    }
}
