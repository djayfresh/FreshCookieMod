package com.fresh.cookies;

import com.minecraft.item.Cookie;

public class Cookie_WM extends Cookie
{

	public Cookie_WM(int par1)
	{
		super(par1, 5, false); // Returns super constructor: par1 is ID
		setUnlocalizedName("White Macadamia Cookie");
		setCreativeTab(CookieMod.cookieTab); // Tells the game what creative mode tab it goes in
		//setTextureName(CookieMod.modid + ":Cookie_WM");
	}
}