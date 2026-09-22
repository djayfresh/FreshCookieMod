package com.fresh.dough;

import com.fresh.cookies.CookieMod;
import com.minecraft.item.SelfSetItem;

public class OR_Dough extends SelfSetItem
{

	public OR_Dough(int par1)
	{
		setUnlocalizedName("Oatmeal Raisin Dough");
		setCreativeTab(CookieMod.cookieTab);
	}

	public void registerIcons()
	{ // Make sure to import IconRegister!

		if (this == CookieMod.or_Dough) //Cookie refers to the class we created and Amethyst is the type
		{
			//The picture filename inside 1.6.4.jar/minecraft/textures
			//this.itemIcon = reg.registerIcon(CookieMod.modid + ":OR_Dough"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}
	}

	@Override
	public String getItemName()
	{
		// TODO Auto-generated method stub
		return getUnlocalizedName().replace("item.", "");
	}
}
