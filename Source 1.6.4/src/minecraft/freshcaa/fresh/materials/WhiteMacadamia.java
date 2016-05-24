package freshcaa.fresh.materials;

import net.minecraft.client.renderer.texture.IconRegister;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.fresh.item.SelfSetFoodItem;
import freshcaa.fresh.item.SelfSetItem;
import freshcaa.fresh.load.ItemLoader;

public class WhiteMacadamia extends SelfSetFoodItem
{

	public WhiteMacadamia(int id)
	{
		super(id, 1, 0, false);
		// TODO Auto-generated constructor stub
		setUnlocalizedName("White Macadamia");
		setCreativeTab(CookieMod.cookieTab);
	}
	
	public void registerIcons(IconRegister reg)
	{ // Make sure to import IconRegister!
		if (itemID == ItemLoader.whiteMacadamia.itemID)
		{
			this.itemIcon = reg.registerIcon(CookieMod.modid + ":whiteMacadamia"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}

	}
}
