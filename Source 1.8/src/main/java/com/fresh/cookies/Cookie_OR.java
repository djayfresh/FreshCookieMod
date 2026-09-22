package com.fresh.cookies;

import com.minecraft.item.Cookie;


public class Cookie_OR extends Cookie
{

	public Cookie_OR(int par1)
	{
		super(par1, 20, false); //Returns super constructor: par1 is ID
		setUnlocalizedName("Oatmeal Raisin Cookie");
		setCreativeTab(CookieMod.cookieTab); //Tells the game what creative mode tab it goes in
	}

	public void registerIcons()
	{ // Make sure to import IconRegister!

		if (this == CookieMod.or_Cookie) //Cookie refers to the class we created and Amethyst is the type
		{
			//The picture filename inside 1.6.4.jar/minecraft/textures
			//this.itemIcon = reg.registerIcon(CookieMod.modid + ":Cookie_OR"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}
	}
}