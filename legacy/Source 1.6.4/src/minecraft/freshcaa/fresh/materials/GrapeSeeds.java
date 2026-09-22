package freshcaa.fresh.materials;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemSeeds;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.fresh.load.ItemLoader;

public class GrapeSeeds extends ItemSeeds
{

	public GrapeSeeds(int par1, int par2, int par3)
	{
		super(par1, par2, par3);
		// TODO Auto-generated constructor stub
		setUnlocalizedName("Grapes");
		setCreativeTab(CookieMod.cookieTab);
	}
	
	public void registerIcons(IconRegister reg)
	{ // Make sure to import IconRegister!
		if (itemID == ItemLoader.grapeSeeds.itemID)
		{
			this.itemIcon = reg.registerIcon(CookieMod.modid + ":Grapes"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}

	}
}
