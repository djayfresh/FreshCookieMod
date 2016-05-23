package freshcaa.fresh.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.ICraftingHandler;
import freshcaa.fresh.load.ItemLoader;

public class CraftingHandler implements ICraftingHandler
{

	@Override
	public void onCrafting(EntityPlayer player, ItemStack item,
			IInventory craftMatrix)
	{
		int itemID = item.getItem().itemID;
		
		if(itemID == Item.sugar.itemID)
		{
			player.triggerAchievement(ItemLoader.gettingStartedAchievement);
		}
		
		if(itemID == ItemLoader.sunTableIdle.blockID)
		{
			player.triggerAchievement(ItemLoader.powerOfTheSunAchievement);
		}
		
		if(itemID == ItemLoader.cookie_Dough.itemID)
		{
			player.triggerAchievement(ItemLoader.gettingDirtyAchievement);
		}
	}

	@Override
	public void onSmelting(EntityPlayer player, ItemStack item)
	{
		int itemID = item.getItem().itemID;
		
		if(itemID == ItemLoader.cc_Cookie.itemID || itemID == ItemLoader.or_Cookie.itemID || 
				itemID == ItemLoader.pb_Cookie.itemID || itemID == ItemLoader.wm_Cookie.itemID || itemID == ItemLoader.wn_Cookie.itemID)
		{
			player.triggerAchievement(ItemLoader.firstCookieAchievement);
		}
		
		if(itemID == ItemLoader.raisin.itemID)
		{
			player.triggerAchievement(ItemLoader.raisinTheRoofAchievement);
		}
	}

}
