package com.minecraft.creativeTabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.fresh.cookies.CookieMod;

public class CookieTab extends CreativeTabs
{

	public CookieTab(String label)
	{
		super(label);
		
	}

	@Override
	public ItemStack getIconItemStack()
	{
		return new ItemStack(CookieMod.cc_Cookie, 1, 0);
	}

	@Override
	public Item getTabIconItem()
	{
		// TODO Auto-generated method stub
		return CookieMod.cc_Cookie;
	}
}
