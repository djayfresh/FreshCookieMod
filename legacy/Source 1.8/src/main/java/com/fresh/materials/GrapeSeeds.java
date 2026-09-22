package com.fresh.materials;

import net.minecraft.block.Block;
import net.minecraft.item.ItemSeeds;

import com.fresh.cookies.CookieMod;

public class GrapeSeeds extends ItemSeeds
{

	public GrapeSeeds(Block crop, Block soil)
	{
		super(crop, soil);
		// TODO Auto-generated constructor stub
		setUnlocalizedName("Grapes");
		setCreativeTab(CookieMod.cookieTab);
	}
	
	public void registerIcons()
	{ // Make sure to import IconRegister!
		if (this == CookieMod.grapeSeeds)
		{
			//this.itemIcon = reg.registerIcon(CookieMod.modid + ":Grapes"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}

	}
}
