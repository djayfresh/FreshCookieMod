package com.fresh.materials;

import com.fresh.cookies.CookieMod;
import com.minecraft.item.SelfSetItem;

public class WhiteMacadamia extends SelfSetItem
{

	public WhiteMacadamia(int par1)
	{
		
		setUnlocalizedName("White Macadamia");
		setCreativeTab(CookieMod.cookieTab);
	}
	
	public void registerIcons()
	{ // Make sure to import IconRegister!
		if (this == CookieMod.whiteMacadamia)
		{
			//this.itemIcon = reg.registerIcon(CookieMod.modid + ":whiteMacadamia"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}

	}
}
