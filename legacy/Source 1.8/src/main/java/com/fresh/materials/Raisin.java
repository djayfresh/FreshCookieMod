package com.fresh.materials;

import com.fresh.cookies.CookieMod;
import com.minecraft.item.SelfSetItem;

public class Raisin extends SelfSetItem
{

	public Raisin(int par1)
	{
		// TODO Auto-generated constructor stub
		setUnlocalizedName("Raisin");
		setCreativeTab(CookieMod.cookieTab);
	}

	public void registerIcons()
	{ // Make sure to import IconRegister!
		if (this == CookieMod.raisin)
		{
			//this.itemIcon = reg.registerIcon(CookieMod.modid + ":raisin"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}

	}

	@Override
	public String getItemName()
	{
		// TODO Auto-generated method stub
		return "Raisin";
	}

}
