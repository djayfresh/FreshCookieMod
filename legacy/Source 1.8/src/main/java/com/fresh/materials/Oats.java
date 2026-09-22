package com.fresh.materials;

import com.fresh.cookies.CookieMod;
import com.minecraft.item.SelfSetItem;

public class Oats extends SelfSetItem {

	public Oats(int par1) {
		// TODO Auto-generated constructor stub
		setUnlocalizedName("Oats");
		setCreativeTab(CookieMod.cookieTab);
	}
	
	public void registerIcons() { // Make sure to import IconRegister!
		if (this == CookieMod.oats) {
			//this.itemIcon = reg.registerIcon(CookieMod.modid + ":oats"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}

	}

	@Override
	public String getItemName()
	{
		// TODO Auto-generated method stub
		return "Oats";
	}

}
