package freshcaa.fresh.dough;

import net.minecraft.client.renderer.texture.IconRegister;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.minecraft.item.SelfSetItem;

public class OR_Dough extends CookieDough
{

	public OR_Dough(int par1)
	{
		super(par1);
		setUnlocalizedName("Oatmeal Raisin Dough");
		setCreativeTab(CookieMod.cookieTab);
	}

	public void registerIcons(IconRegister reg)
	{ // Make sure to import IconRegister!

		if (itemID == CookieMod.or_Dough.itemID) //Cookie refers to the class we created and Amethyst is the type
		{
			//The pictue filename inside 1.6.4.jar/minecraft/textures
			this.itemIcon = reg.registerIcon(CookieMod.modid + ":OR_Dough"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}
	}

	@Override
	public String getItemName()
	{
		// TODO Auto-generated method stub
		return getUnlocalizedName().replace("item.", "");
	}
}
