package freshcaa.fresh.materials;

import net.minecraft.client.renderer.texture.IconRegister;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.minecraft.item.SelfSetItem;

public class Oats extends SelfSetItem {

	public Oats(int par1) {
		super(par1);
		// TODO Auto-generated constructor stub
		setUnlocalizedName("Oats");
		setCreativeTab(CookieMod.cookieTab);
	}
	
	public void registerIcons(IconRegister reg) { // Make sure to import IconRegister!
		if (itemID == CookieMod.oats.itemID) {
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
