package com.fresh.cookies;

import com.minecraft.item.Cookie;


public class Cookie_CC extends Cookie
{

	public Cookie_CC(int par1)
	{
		super(par1, 5, false); //Returns super constructor: par1 is ID
		setUnlocalizedName("Chocolate Chip Cookie");
		setCreativeTab(CookieMod.cookieTab); //Tells the game what creative mode tab it goes in
	}

	public void registerIcons()
	{ // Make sure to import IconRegister!

		if (this == CookieMod.cc_Cookie) //Cookie refers to the class we created and Amethyst is the type
		{
			//The picture filename inside 1.6.4.jar/minecraft/textures
			//registerIcon(CookieMod.modid + ":Cookie_CC"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}
	}
}