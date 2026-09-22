package freshcaa.fresh.achievements;

import freshcaa.fresh.cookies.CookieMod;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class CookieAchievementPage extends AchievementPage
{
	public CookieAchievementPage(Achievement[] achievements)
	{
		super(CookieMod.modid, achievements);
	}
}