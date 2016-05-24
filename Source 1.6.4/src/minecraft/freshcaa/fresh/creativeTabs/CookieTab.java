package freshcaa.fresh.creativeTabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.fresh.load.ItemLoader;

public class CookieTab extends CreativeTabs
{

	public CookieTab(String label)
	{
		super(label);
		
	}

	public ItemStack getIconItemStack()
	{
		return new ItemStack(ItemLoader.cc_Cookie, 1, 0);
	}
}
