package freshcaa.fresh.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * 
 * @author DFresh
 *A new Item class that allows for setup before load
 */
public abstract class SelfSetItem extends Item{

	public SelfSetItem(int par1) {
		super(par1);
		setUnlocalizedName("The name of your Item");
		setCreativeTab(CreativeTabs.tabMaterials); //Change to represent the item you'll be creating
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
