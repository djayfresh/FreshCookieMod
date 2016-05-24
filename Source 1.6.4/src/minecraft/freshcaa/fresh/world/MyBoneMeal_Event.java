package freshcaa.fresh.world;

import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.BonemealEvent;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.fresh.load.ItemLoader;
import freshcaa.fresh.trees.MyModSaplings;

public class MyBoneMeal_Event
{
	@ForgeSubscribe
	public void usedBonemeal(BonemealEvent event)
	{
		if(event.ID == ItemLoader.pecanSapling.blockID)
		{
			if(!event.world.isRemote)
			{
				((MyModSaplings)ItemLoader.pecanSapling).growTree(event.world, event.X, event.Y, event.Z, event.world.rand);
			}
		}
		if(event.ID == ItemLoader.macadamiaSapling.blockID)
		{
			if(!event.world.isRemote)
			{
				((MyModSaplings)ItemLoader.macadamiaSapling).growTree(event.world, event.X, event.Y, event.Z, event.world.rand);
			}
		}
	}
}
