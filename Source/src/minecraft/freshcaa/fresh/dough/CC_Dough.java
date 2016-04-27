package freshcaa.fresh.dough;

import net.minecraft.client.renderer.texture.IconRegister;
import freshcaa.fresh.cookies.CookieMod;

public class CC_Dough extends CookieDough
{

	public CC_Dough(int par1)
	{
		super(par1);
		setUnlocalizedName("Chocolate Chip Cookie Dough");
		setCreativeTab(CookieMod.cookieTab);
		// TODO Auto-generated constructor stub
	}

	public void registerIcons(IconRegister reg)
	{ // Make sure to import IconRegister!

		if (itemID == CookieMod.cc_Dough.itemID) //Cookie refers to the class we created and Amethyst is the type
		{
			//The pictue filename inside 1.6.4.jar/minecraft/textures
			this.itemIcon = reg.registerIcon(CookieMod.modid + ":CC_Dough"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}
	}
}
