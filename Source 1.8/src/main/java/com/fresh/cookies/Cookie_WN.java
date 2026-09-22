package com.fresh.cookies;

import com.fresh.pirates.Reference;
import com.minecraft.item.Cookie;

public class Cookie_WN extends Cookie
{

	public Cookie_WN(int par1)
	{
		super(par1, 5, true); // Returns super constructor: par1 is ID
		setUnlocalizedName("Pecan Cookie");
		setCreativeTab(CookieMod.cookieTab); // Tells the game what creative mode tab it goes in
		//setTextureName(Reference.MODID + ":Cookie_WN"); 
	}
}