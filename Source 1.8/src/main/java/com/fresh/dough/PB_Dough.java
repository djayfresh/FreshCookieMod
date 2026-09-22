package com.fresh.dough;

import com.fresh.cookies.CookieMod;
import com.minecraft.item.SelfSetItem;

public class PB_Dough extends SelfSetItem
{

	public PB_Dough(int par1)
	{
		setUnlocalizedName("Peanut Butter Cookie Dough");
		setCreativeTab(CookieMod.cookieTab);
	}
	
	public void registerIcons()
	{ // Make sure to import IconRegister!
		if (this == CookieMod.pb_Dough)
		{
			//this.itemIcon = reg.registerIcon(CookieMod.modid + ":PB_Dough"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}

	}

	@Override
	public String getItemName()
	{
		// TODO Auto-generated method stub
		return getUnlocalizedName().replace("item.", "");
	}
}
