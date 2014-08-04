package freshcaa.fresh.materials;

import net.minecraft.client.renderer.texture.IconRegister;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.minecraft.item.SelfSetItem;

public class Raisin extends SelfSetItem
{

	public Raisin(int par1)
	{
		super(par1);
		// TODO Auto-generated constructor stub
		setUnlocalizedName("Raisin");
		setCreativeTab(CookieMod.cookieTab);
	}

	public void registerIcons(IconRegister reg)
	{ // Make sure to import IconRegister!
		if (itemID == CookieMod.raisin.itemID)
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
