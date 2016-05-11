package freshcaa.fresh.events;

import freshcaa.fresh.load.ItemLoader;
import net.minecraft.item.Item;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class AchievementsEvent
{
	@ForgeSubscribe
	public void itemPickedUp(EntityItemPickupEvent event)
	{
		System.out.println("Item Pickup event");
		int itemId = event.item.getEntityItem().itemID;
		if(itemId == ItemLoader.macadamiaLog.blockID || itemId == ItemLoader.pecanLog.blockID)
		{
			event.entityPlayer.triggerAchievement(AchievementList.mineWood);
		}
		
		if(itemId == Item.wheat.itemID || itemId == Item.egg.itemID){
			event.entityPlayer.triggerAchievement(ItemLoader.gettingStartedAchievement);
		}
	}
}
