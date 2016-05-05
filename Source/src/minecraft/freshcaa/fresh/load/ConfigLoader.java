package freshcaa.fresh.load;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

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
	public static int pecanLeafID;
	public static int pecanSaplingID;
	public static int macadamiaLogID;
	public static int macadamiaLeafID;
	public static int macadamiaSaplingID;
	

	private static String CategoryItem = "item";
	private static String CategoryBlock = "block";
	private static String CategoryMaterial = "material";
	
	private static int ConfigOffsetValue = 256;
	
	public static void LoadConfig(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
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
		
		peanutPlantID = getBlockId(config, "Peanut Plant", 2117);
		grapeVineID = getBlockId(config, "Grape Vines", 2118);
		sunTableID = getBlockId(config, "Sun Drying Table", 2119);
		sunTableIdleID = getBlockId(config, "Sun Drying Table Idle", 2120);
		pecanLogID = getBlockId(config, "Pecan Log", 2121);
		pecanLeafID = getBlockId(config, "Pecan Leaf", 2122);
		pecanSaplingID = getBlockId(config, "Pecan Sapling", 2123);
		macadamiaLogID = getBlockId(config, "Macadamia Log", 2124);
		macadamiaLeafID = getBlockId(config, "Macadamia Leaf", 2125);
		macadamiaSaplingID = getBlockId(config, "Macadamia Sapling", 2126);
		
		pecanID = getItemId(config, CategoryMaterial, "Pecan", 2105);
		raisinID = getItemId(config, CategoryMaterial, "Raisin", 2106);
		oatsID = getItemId(config, CategoryMaterial, "Oats", 2107);		
		whiteMacadamiaID = getItemId(config, CategoryMaterial, "White Macadamia", 2108);
		
		config.save();
	}
	
	private static int getItemId(Configuration config, String property, int defaultValue)
	{
		return config.get("item", property, defaultValue).getInt() - ConfigOffsetValue;
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
