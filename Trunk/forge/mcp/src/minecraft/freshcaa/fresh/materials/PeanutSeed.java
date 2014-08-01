package freshcaa.fresh.materials;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemSeeds;
import freshcaa.fresh.cookies.CookieMod;

public class PeanutSeed extends ItemSeeds
{

	public PeanutSeed(int par1, int par2, int par3)
	{
		super(par1, par2, par3);
		setUnlocalizedName("Peanut Seeds");
		setCreativeTab(CookieMod.cookieTab);
	}
	
	public void registerIcons(IconRegister reg)
	{ // Make sure to import IconRegister!
		if (itemID == CookieMod.peanutSeeds.itemID)
		{
			this.itemIcon = reg.registerIcon(CookieMod.modid + ":PeanutSeed"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}

	}
}
