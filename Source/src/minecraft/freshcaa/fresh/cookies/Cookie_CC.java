package freshcaa.fresh.cookies;

import net.minecraft.client.renderer.texture.IconRegister;
import freshcaa.fresh.load.ConfigLoader;
import freshcaa.fresh.load.ItemLoader;
import freshcaa.minecraft.item.Cookie;

public class Cookie_CC extends Cookie
{

	public Cookie_CC(int id)
	{
		super(id, ConfigLoader.chocolateCookieHeal, false); //Returns super constructor: par1 is ID
		setUnlocalizedName("Chocolate Chip Cookie");
		setCreativeTab(CookieMod.cookieTab); //Tells the game what creative mode tab it goes in
	}

	public void registerIcons(IconRegister reg)
	{ // Make sure to import IconRegister!

		if (itemID == ItemLoader.cc_Cookie.itemID) //Cookie refers to the class we created and Amethyst is the type
		{
			//The pictue filename inside 1.6.4.jar/minecraft/textures
			this.itemIcon = reg.registerIcon(CookieMod.modid + ":Cookie_CC"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}
	}
}