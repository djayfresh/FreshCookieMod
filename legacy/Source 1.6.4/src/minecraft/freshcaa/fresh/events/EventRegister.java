package freshcaa.fresh.events;

import net.minecraftforge.common.MinecraftForge;

public class EventRegister
{
	public static void Register() 
	{
		MinecraftForge.EVENT_BUS.register(new BoneMealEvent());
		MinecraftForge.EVENT_BUS.register(new AchievementsEvent());
	}
}