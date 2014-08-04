package freshcaa.minecraft.creativeTabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import freshcaa.fresh.cookies.CookieMod;

public class CookieTab extends CreativeTabs
{

	public CookieTab(String label)
	{
		super(label);
		
	}

	public ItemStack getIconItemStack()
	{
		return new ItemStack(CookieMod.cc_Cookie, 1, 0);
	}
}
