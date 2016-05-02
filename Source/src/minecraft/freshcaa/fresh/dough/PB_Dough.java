package freshcaa.fresh.dough;

import net.minecraft.client.renderer.texture.IconRegister;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.minecraft.item.SelfSetItem;

public class PB_Dough extends CookieDough
{

	public PB_Dough(int par1)
	{
		super(par1);
		setUnlocalizedName("Peanut Butter Cookie Dough");
		setCreativeTab(CookieMod.cookieTab);
	}
	
	public void registerIcons(IconRegister reg)
	{ // Make sure to import IconRegister!
		if (itemID == CookieMod.pb_Dough.itemID)
		{
			this.itemIcon = reg.registerIcon(CookieMod.modid + ":PB_Dough"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}

	}

	@Override
	public String getItemName()
	{
		// TODO Auto-generated method stub
		return getUnlocalizedName().replace("item.", "");
	}
}
