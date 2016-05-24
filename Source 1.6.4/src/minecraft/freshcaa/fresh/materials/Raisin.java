package freshcaa.fresh.materials;

import net.minecraft.client.renderer.texture.IconRegister;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.fresh.item.SelfSetFoodItem;
import freshcaa.fresh.item.SelfSetItem;
import freshcaa.fresh.load.ItemLoader;

public class Raisin extends SelfSetFoodItem
{

	public Raisin(int id)
	{
		super(id, 1, 1, false);
		// TODO Auto-generated constructor stub
		setUnlocalizedName("Raisin");
		setCreativeTab(CookieMod.cookieTab);
	}

	public void registerIcons(IconRegister reg)
	{ // Make sure to import IconRegister!
		if (itemID == ItemLoader.raisin.itemID)
		{
			this.itemIcon = reg.registerIcon(CookieMod.modid + ":raisin"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}

	}

	@Override
	public String getItemName()
	{
		// TODO Auto-generated method stub
		return "Raisin";
	}

}
