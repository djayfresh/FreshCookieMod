package freshcaa.fresh.materials;

import net.minecraft.client.renderer.texture.IconRegister;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.fresh.load.ItemLoader;
import freshcaa.minecraft.item.SelfSetFoodItem;
import freshcaa.minecraft.item.SelfSetItem;

public class Oats extends SelfSetFoodItem {

	public Oats(int par1) {
		super(par1, 2, 1, false);
		// TODO Auto-generated constructor stub
		setUnlocalizedName("Oats");
		setCreativeTab(CookieMod.cookieTab);
	}
	
	public void registerIcons(IconRegister reg) { // Make sure to import IconRegister!
		if (itemID == ItemLoader.oats.itemID) {
			this.itemIcon = reg.registerIcon(CookieMod.modid + ":oats"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}

	}

	@Override
	public String getItemName()
	{
		// TODO Auto-generated method stub
		return "Oats";
	}

}
