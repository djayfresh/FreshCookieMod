package freshcaa.fresh.achievements;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

public class GroupAchievementBase extends Achievement
{
	protected ArrayList<Achievement> parentAchievements = new ArrayList<Achievement>();
	
	public GroupAchievementBase(int id, String name, int x, int y,
			ItemStack item, Achievement[] parents)
	{
		super(id, name, x, y, item, parents[0]);
		
		parentAchievements.addAll(Arrays.asList(parents));
		// TODO Auto-generated constructor stub
	}
}
