package freshcaa.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;

/**
 * 
 * @author DFresh
 *A new Item class that allows for setup before load
 */
public abstract class SelfSetFoodItem extends ItemFood {

	public SelfSetFoodItem(int id, int health, int saturation, boolean isWolfFood) {
		super(id, health, saturation, isWolfFood);
		setUnlocalizedName("The name of your Item");
		setCreativeTab(CreativeTabs.tabFood); //Change to represent the item you'll be creating
		// TODO Auto-generated constructor stub
	}

	/**
	 * Using the UnlocalizedName, parsed to remove "item."
	 * @return The items name
	 */
	public String getItemName()
	{
		return getUnlocalizedName().replace("item.", "");
	}
}
