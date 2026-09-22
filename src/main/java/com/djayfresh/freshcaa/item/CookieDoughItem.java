package com.djayfresh.freshcaa.item;

import com.djayfresh.freshcaa.Config;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Raw cookie dough. Edible, but eating it may poison you (5 seconds) depending on config. */
public class CookieDoughItem extends Item {
    private static final int POISON_TICKS = 100;

    public CookieDoughItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(itemStack, level, entity);
        if (!level.isClientSide()
                && Config.COOKIE_DOUGH_POISONOUS.getAsBoolean()
                && entity.getRandom().nextDouble() < Config.COOKIE_DOUGH_POISON_CHANCE.getAsDouble()) {
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_TICKS, 0));
        }
        return result;
    }
}
