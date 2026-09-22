package com.fresh.dough;

import com.fresh.cookies.CookieMod;
import com.minecraft.item.SelfSetItem;

public class CC_Dough extends SelfSetItem
{

	public CC_Dough(int par1)
	{
		setUnlocalizedName("Chocolate Chip Cookie Dough");
		setCreativeTab(CookieMod.cookieTab);
		// TODO Auto-generated constructor stub
	}

	public void registerIcons()
	{ // Make sure to import IconRegister!

		if (this == CookieMod.cc_Dough) //Cookie refers to the class we created and Amethyst is the type
		{
			//The pictue filename inside 1.6.4.jar/minecraft/textures
			//this.itemIcon = reg.registerIcon(CookieMod.modid + ":CC_Dough"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}
	}
}
