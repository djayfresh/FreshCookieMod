package freshcaa.fresh.materials;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemFood;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.minecraft.item.SelfSetFoodItem;

public class Pecan extends SelfSetFoodItem
{

	public Pecan(int par1)
	{
		super(par1, 1, 0, false);
		// TODO Auto-generated constructor stub
		setUnlocalizedName("Pecan");
		setCreativeTab(CookieMod.cookieTab);
	}
	
	public void registerIcons(IconRegister reg)
	{ // Make sure to import IconRegister!
		if (itemID == CookieMod.pecan.itemID)
		{
			this.itemIcon = reg.registerIcon(CookieMod.modid + ":pecan"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}

	}
}
