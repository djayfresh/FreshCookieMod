package com.minecraft.world;

import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.fml.common.Mod.EventHandler;

import com.fresh.cookies.CookieMod;
import com.fresh.trees.MyModSaplings;

public class MyBoneMeal_Event
{
	@EventHandler
	public void usedBonemeal(BonemealEvent event)
	{
		if(!event.world.isRemote)
		{
			((MyModSaplings)CookieMod.pecanSapling).growTree(event.world, event.pos, event.world.rand);
		}
		if(!event.world.isRemote)
		{
			((MyModSaplings)CookieMod.macadamiaSapling).growTree(event.world, event.pos, event.world.rand);
		}
	}
}
