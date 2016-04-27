package freshcaa.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;

public abstract class Cookie extends ItemFood
{

	public Cookie(int itemID, int healAmount, boolean wolfLove)
	{
		super(itemID, healAmount, wolfLove);
		// TODO Auto-generated constructor stub
		setCreativeTab(CreativeTabs.tabFood); // Tells the game what creative
												// mode tab it goes in
	}

	/**
	 * Using the UnlocalizedName, parsed to remove "item."
	 * 
	 * @return The items name
	 */
	public String getItemName()
	{
		return getUnlocalizedName().replace("item.", "");
	}
}
