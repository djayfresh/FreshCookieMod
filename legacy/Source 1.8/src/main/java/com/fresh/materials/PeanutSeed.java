package com.fresh.materials;

import net.minecraft.block.Block;
import net.minecraft.item.ItemSeeds;

import com.fresh.cookies.CookieMod;

public class PeanutSeed extends ItemSeeds
{

	public PeanutSeed(Block crop, Block soil)
	{
		super(crop, soil);
		setUnlocalizedName("Peanut Seeds");
		setCreativeTab(CookieMod.cookieTab);
	}
	
	public void registerIcons()
	{ // Make sure to import IconRegister!
		if (this == CookieMod.peanutSeeds)
		{
			//this.itemIcon = reg.registerIcon(CookieMod.modid + ":PeanutSeed"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}

	}
}
