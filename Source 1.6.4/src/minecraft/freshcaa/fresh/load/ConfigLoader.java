package freshcaa.fresh.load;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * ConfigLoader - Singleton. Using the current preloading event, create/read the current configuration file. 
 * Adding properties to this class must be made static. 
 */
public class ConfigLoader
{
	public static int cc_CookieID;
	public static int wm_CookieID;
	public static int pb_CookieID;
	public static int wn_CookieID;
	public static int or_CookieID;
	public static int pecanID;
	public static int raisinID;
	public static int oatsID;
	public static int whiteMacadamiaID;
	public static int cookie_DoughID;
	public static int cc_DoughID;
	public static int or_DoughID;
	public static int wm_DoughID;
	public static int wn_DoughID;
	public static int pb_DoughID;
	public static int peanutSeedsID;
	public static int grapeSeedsID;
	public static int peanutPlantID;
	public static int grapeVineID;
	public static int sunTableID;
	public static int sunTableIdleID;
	public static int pecanLogID;
	public static int pecanPlankID;
	public static int pecanLeafID;
	public static int pecanSaplingID;
	public static int macadamiaLogID;
	public static int macadamiaPlankID;
	public static int macadamiaLeafID;
	public static int macadamiaSaplingID;
	
	public static int peanutSeedDropWeight;
	public static int grapeSeedDropWeight;
	public static int macadamiaSaplingDropWeight;
	public static int pecanSaplingDropWeight;
	
	public static boolean isCookieDoughPoisonous;
	
	public static int chocolateCookieHeal;
	public static int oatmealRaisinCookieHeal;
	public static int peanutButterCookieHeal;
	public static int pecanCookieHeal;
	public static int macadamiaCookieHeal;
	
	public static int gettingStartedAchievement;
	public static int gettingDirtyAchievement;
	public static int powerOfTheSunAchievement;
	public static int firstCookieAchievement;
	public static int raisinTheRoofAchievement;
	public static int pecanHarvestAchievement;
	public static int macadamiaDanceAchievement;

	private static String CategoryItem = "item";
	private static String CategoryBlock = "block";
	private static String CategoryMaterial = "material";
	private static String CategoryOther = "other";
	private static String CategoryFood = "food";
	private static String CategoryAchievement = "achievement";
	
	private static int ConfigOffsetValue = 256;
	
	public static void LoadConfig(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		loadItems(config);
		
		loadBlocks(config);
				
		loadDropWeights(config);
		
		loadCookieData(config);
		
		loadAchievements(config);
		
		config.save();
	}

	private static void loadAchievements(Configuration config)
	{
		gettingStartedAchievement = config.get(CategoryAchievement, "Getting Started", 200).getInt();
		gettingDirtyAchievement = config.get(CategoryAchievement, "Getting Your Hands Dirty", 201).getInt();
		powerOfTheSunAchievement = config.get(CategoryAchievement, "Power Of The Sun", 202).getInt();
		firstCookieAchievement = config.get(CategoryAchievement, "First Cookie", 203).getInt();
		raisinTheRoofAchievement = config.get(CategoryAchievement, "Raisin The Roof", 204).getInt();
		pecanHarvestAchievement = config.get(CategoryAchievement, "Pecan Harvest", 205).getInt();
		macadamiaDanceAchievement = config.get(CategoryAchievement, "Macadamia Dance", 206).getInt();
	}

	private static void loadCookieData(Configuration config)
	{
		isCookieDoughPoisonous = config.get(CategoryOther, "Cookie Dough Poisonous", true, 
				"Does Cookie Dough have a chance to poison you").getBoolean(true);
		
		chocolateCookieHeal = config.get(CategoryFood, "Chocolate Chip Cookie Heal", 4).getInt();
		oatmealRaisinCookieHeal = config.get(CategoryFood, "Oatmeal Raisin Cookie Heal", 8).getInt();
		peanutButterCookieHeal = config.get(CategoryFood, "Peanut Butter Cookie Heal", 8).getInt();
		pecanCookieHeal = config.get(CategoryFood, "Pecan Cookie Heal", 5).getInt();
		macadamiaCookieHeal = config.get(CategoryFood, "White Macadamia Cookie Heal", 5).getInt();
	}

	private static void loadDropWeights(Configuration config)
	{
		peanutSeedDropWeight = config.get(CategoryOther, "Peanut Seed Drop Weight", 10, 
				"Grass drop weight, wheat seed default 10").getInt();
		grapeSeedDropWeight = config.get(CategoryOther, "Grape Seed Drop Weight", 10, 
				"Grass drop weight, wheat seed default 10").getInt();
		macadamiaSaplingDropWeight = config.get(CategoryOther, "Macadamia Sapling Drop Weight", 10, 
				"Grass drop weight, wheat seed default 10").getInt();
		pecanSaplingDropWeight = config.get(CategoryOther, "Pecan Sapling Drop Weight", 10, 
				"Grass drop weight, wheat seed default 10").getInt();
	}

	private static void loadBlocks(Configuration config)
	{
		peanutPlantID = getBlockId(config, "Peanut Plant", 2117);
		grapeVineID = getBlockId(config, "Grape Vines", 2118);
		sunTableID = getBlockId(config, "Sun Drying Table", 2119);
		sunTableIdleID = getBlockId(config, "Sun Drying Table Idle", 2120);
		pecanLogID = getBlockId(config, "Pecan Log", 2121);
		pecanPlankID = getBlockId(config, "Pecan Plank", 2127);
		pecanLeafID = getBlockId(config, "Pecan Leaf", 2122);
		pecanSaplingID = getBlockId(config, "Pecan Sapling", 2123);
		macadamiaLogID = getBlockId(config, "Macadamia Log", 2124);
		macadamiaPlankID = getBlockId(config, "Macadamia Plank", 2128);
		macadamiaLeafID = getBlockId(config, "Macadamia Leaf", 2125);
		macadamiaSaplingID = getBlockId(config, "Macadamia Sapling", 2126);
	}
	
	private static void loadItems(Configuration config)
	{
		cc_CookieID = getItemId(config, "Chocolate Chip Cookie", 2100);
		wm_CookieID = getItemId(config, "Pecan Cookie", 2101);
		pb_CookieID = getItemId(config, "Peanut Butter Cookie", 2102);
		wn_CookieID = getItemId(config, "White Macadamia Cookie", 2103);
		or_CookieID = getItemId(config, "Oatmeal Raisin Cookie", 2104);
		
		cookie_DoughID = getItemId(config, "Cookie Dough", 2109);
		cc_DoughID = getItemId(config, "Chocolate Chip Dough", 2110);
		or_DoughID = getItemId(config, "Oatmeal Raisin Dough", 2111);
		wm_DoughID = getItemId(config, "White Macadamia Dough", 2112);
		wn_DoughID = getItemId(config, "Pecan Dough", 2113);
		pb_DoughID = getItemId(config, "Peanut Butter Dough", 2114);
		
		peanutSeedsID = getItemId(config, "Peanuts", 2115);
		grapeSeedsID = getItemId(config, "Grapes", 2116);
		
		pecanID = getItemId(config, CategoryMaterial, "Pecan", 2105);
		raisinID = getItemId(config, CategoryMaterial, "Raisin", 2106);
		oatsID = getItemId(config, CategoryMaterial, "Oats", 2107);		
		whiteMacadamiaID = getItemId(config, CategoryMaterial, "White Macadamia", 2108);
	}
	
	private static int getItemId(Configuration config, String property, int defaultValue)
	{
		return config.get(CategoryItem, property, defaultValue).getInt() - ConfigOffsetValue;
	}
	
	private static int getItemId(Configuration config, String category, String property, int defaultValue)
	{
		return config.get(category, property, defaultValue).getInt() - ConfigOffsetValue;
	}
	
	private static int getBlockId(Configuration config, String property, int defaultValue)
	{
		return config.getBlock(property, defaultValue).getInt();
	}
}
