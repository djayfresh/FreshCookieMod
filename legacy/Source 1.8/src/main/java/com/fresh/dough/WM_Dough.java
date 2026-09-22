package com.fresh.dough;

import com.fresh.cookies.CookieMod;
import com.minecraft.item.SelfSetItem;

public class WM_Dough extends SelfSetItem
{
	public WM_Dough(int par1)
	{
		setUnlocalizedName("White Macadamia Dough");
		// TODO Auto-generated constructor stub
		setCreativeTab(CookieMod.cookieTab);
	}

	public void registerIcons()
	{ // Make sure to import IconRegister!

		if (this == CookieMod.wm_Dough) //Cookie refers to the class we created and Amethyst is the type
		{
			//The pictue filename inside 1.6.4.jar/minecraft/textures
			//this.itemIcon = reg.registerIcon(CookieMod.modid + ":WM_Dough"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}
	}

	@Override
	public String getItemName()
	{
		// TODO Auto-generated method stub
		return "White Macadamia Dough";
	}
}
