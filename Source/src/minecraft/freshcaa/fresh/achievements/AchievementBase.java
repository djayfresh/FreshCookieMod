package freshcaa.fresh.achievements;

import freshcaa.fresh.cookies.CookieMod;
import freshcaa.fresh.load.ItemLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

public class AchievementBase extends Achievement
{
	public AchievementBase(int id, String name, int x, int y,
			ItemStack item, Achievement parentAchievement)
	{
		super(id,CookieMod.modid + "." + name, x, y, item, parentAchievement);
	}
}
