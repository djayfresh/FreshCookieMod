package freshcaa.fresh.dough;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.potion.Potion;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.fresh.load.ConfigLoader;
import freshcaa.fresh.load.ItemLoader;
import freshcaa.minecraft.item.SelfSetFoodItem;

public class CookieDough extends SelfSetFoodItem
{

	public CookieDough(int id)
	{
		super(id, 1, 0, false );
		setUnlocalizedName("Cookie Dough");
		setCreativeTab(CookieMod.cookieTab);
		if(ConfigLoader.isCookieDoughPoisonous)
		{
			setPotionEffect(Potion.poison.id, 5, 0, 0.4F);
		}
		// TODO Auto-generated constructor stub
	}

	public void registerIcons(IconRegister reg)
	{ // Make sure to import IconRegister!

		if (itemID == ItemLoader.cookie_Dough.itemID) //Cookie refers to the class we created and Amethyst is the type
		{
			//The pictue filename inside 1.6.4.jar/minecraft/textures
			this.itemIcon = reg.registerIcon(CookieMod.modid + ":Cookie_Dough"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}
	}

	@Override
	public String getItemName()
	{
		// TODO Auto-generated method stub
		return getUnlocalizedName().replace("item.", "");
	}
}
