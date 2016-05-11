package freshcaa.fresh.load;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import freshcaa.fresh.achievements.AchievementBase;
import freshcaa.fresh.achievements.CookieAchievementPage;
import freshcaa.fresh.achievements.FirstCookieAchievement;
import freshcaa.fresh.achievements.GettingDirtyAchievement;
import freshcaa.fresh.achievements.GettingStartedAchievement;
import freshcaa.fresh.achievements.PowerOfTheSunAchievement;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.fresh.cookies.Cookie_CC;
import freshcaa.fresh.cookies.Cookie_OR;
import freshcaa.fresh.cookies.Cookie_PB;
import freshcaa.fresh.cookies.Cookie_WM;
import freshcaa.fresh.cookies.Cookie_WN;
import freshcaa.fresh.dough.CC_Dough;
import freshcaa.fresh.dough.CookieDough;
import freshcaa.fresh.dough.OR_Dough;
import freshcaa.fresh.dough.PB_Dough;
import freshcaa.fresh.dough.WM_Dough;
import freshcaa.fresh.dough.WN_Dough;
import freshcaa.fresh.events.CraftingHandler;
import freshcaa.fresh.materials.GrapeSeeds;
import freshcaa.fresh.materials.Oats;
import freshcaa.fresh.materials.PeanutSeed;
import freshcaa.fresh.materials.Pecan;
import freshcaa.fresh.materials.Raisin;
import freshcaa.fresh.materials.WhiteMacadamia;
import freshcaa.fresh.trees.MyModLeafs;
import freshcaa.fresh.trees.MyModLogs;
import freshcaa.fresh.trees.MyModSaplings;
import freshcaa.minecraft.block.GrapeVine;
import freshcaa.minecraft.block.PeanutPlant;
import freshcaa.minecraft.block.SunTable;
import freshcaa.minecraft.item.Cookie;
import freshcaa.minecraft.item.SelfSetFoodItem;

/**
 * ItemLoader - Singleton. Uses ConfigLoader to grab ids set during load.
 * Add new items here to keep one place for all items registered
 * 
 * @TODO Break out blocks into its own loader
 */
public class ItemLoader
{
	//Cookies
	public static Cookie cc_Cookie;
	public static Cookie wm_Cookie;
	public static Cookie pb_Cookie;
	public static Cookie wn_Cookie;
	public static Cookie or_Cookie;
	
	//Food
	public static SelfSetFoodItem pecan;
	public static SelfSetFoodItem raisin;
	public static SelfSetFoodItem oats;
	public static SelfSetFoodItem whiteMacadamia;
	public static SelfSetFoodItem cookie_Dough;
	public static SelfSetFoodItem cc_Dough;
	public static SelfSetFoodItem or_Dough;
	public static SelfSetFoodItem pb_Dough;
	public static SelfSetFoodItem wm_Dough;
	public static SelfSetFoodItem wn_Dough;
	
	//Seeds
	public static Item peanutSeeds;
	public static Item grapeSeeds;
	
	//Blocks
	public static Block peanutPlant;
	public static Block grapeVine;
	public static Block sunTable;
	public static Block sunTableIdle;
	
	//Trees
	public static Block pecanLog;
	public static Block pecanLeaf;
	public static Block pecanSapling;
	public static Block macadamiaLog;
	public static Block macadamiaLeaf;
	public static Block macadamiaSapling;
	
	//Achievements
	public static AchievementBase gettingStartedAchievement;
	public static AchievementBase gettingDirtyAchievement;
	public static AchievementBase powerOfTheSunAchievement;
	public static AchievementBase firstCookieAchievement;
	public static AchievementBase raisinTheRoofAchievement;
	public static AchievementPage cookieAchievementPage;
	
	public static void Load()
	{
		AddItems();
		RegisterItems();
		
		AddAchievements();
		RegisterAchievements();
		
		RegisterOther();
		
		AddRecipes();
		AddLanguages();
	}
	
	private static void RegisterOther()
	{
		GameRegistry.registerCraftingHandler(new CraftingHandler());
	}

	private static void RegisterAchievements()
	{
		gettingStartedAchievement.registerAchievement();
		gettingDirtyAchievement.registerAchievement();
		powerOfTheSunAchievement.registerAchievement();
		firstCookieAchievement.registerAchievement();
		raisinTheRoofAchievement.registerAchievement();
		
		AchievementPage.registerAchievementPage(cookieAchievementPage);
	}

	private static void AddAchievements()
	{
		gettingStartedAchievement = new GettingStartedAchievement(ConfigLoader.gettingStartedAchievement, 
				"gettingstarted", 0, 0, new ItemStack(Item.wheat));
		gettingDirtyAchievement = new GettingDirtyAchievement(ConfigLoader.gettingDirtyAchievement, 
				"gettingdirty", 2, 0, new ItemStack(ItemLoader.cookie_Dough), gettingStartedAchievement);
		powerOfTheSunAchievement = new PowerOfTheSunAchievement(ConfigLoader.powerOfTheSunAchievement, 
				"powerofthesun", -3, -2, new ItemStack(ItemLoader.sunTable), AchievementList.diamonds);
		firstCookieAchievement = new FirstCookieAchievement(ConfigLoader.firstCookieAchievement, 
				"firstcookie", 2, -2, new ItemStack(Item.cookie), gettingDirtyAchievement);
		raisinTheRoofAchievement = new FirstCookieAchievement(ConfigLoader.raisinTheRoofAchievement, 
				"raisintheroof", 2, 1, new ItemStack(Item.cookie), gettingDirtyAchievement);
		
		cookieAchievementPage = new CookieAchievementPage(new Achievement[] {
				gettingStartedAchievement, 
				gettingDirtyAchievement,
				firstCookieAchievement,
				powerOfTheSunAchievement
			});
	}

	private static void AddItems() 
	{
		//Cookies
		cc_Cookie = new Cookie_CC(ConfigLoader.cc_CookieID);
		wm_Cookie = new Cookie_WM(ConfigLoader.wm_CookieID);
		wn_Cookie = new Cookie_WN(ConfigLoader.wn_CookieID);
		or_Cookie = new Cookie_OR(ConfigLoader.or_CookieID);
		pb_Cookie = new Cookie_PB(ConfigLoader.pb_CookieID);
		
		//Cookie Materials
		raisin = new Raisin(ConfigLoader.raisinID);
		oats = new Oats(ConfigLoader.oatsID);
		pecan = new Pecan(ConfigLoader.pecanID);
		whiteMacadamia = new WhiteMacadamia(ConfigLoader.whiteMacadamiaID);
		//Cookie Dough
		cookie_Dough = new CookieDough(ConfigLoader.cookie_DoughID);
		cc_Dough = new CC_Dough(ConfigLoader.cc_DoughID);
		or_Dough = new OR_Dough(ConfigLoader.or_DoughID);
		pb_Dough = new PB_Dough(ConfigLoader.pb_DoughID);
		wm_Dough = new WM_Dough(ConfigLoader.wm_DoughID);
		wn_Dough = new WN_Dough(ConfigLoader.wn_DoughID);
		
		// define blocks
		sunTable = new SunTable(ConfigLoader.sunTableID, true);
		sunTableIdle = new SunTable(ConfigLoader.sunTableIdleID, false);
		
		pecanSapling = new MyModSaplings(ConfigLoader.pecanSaplingID)
			.setUnlocalizedName("Pecan Sapling")
			.setCreativeTab(CookieMod.cookieTab);
		
		pecanLog = new MyModLogs(ConfigLoader.pecanLogID)
			.setUnlocalizedName("Pecan Log")
			.setCreativeTab(CookieMod.cookieTab);
		
		pecanLeaf = new MyModLeafs(ConfigLoader.pecanLeafID)
			.setUnlocalizedName("Pecan Leaf");
		
		macadamiaSapling = new MyModSaplings(ConfigLoader.macadamiaSaplingID)
			.setUnlocalizedName("Macadamia Sapling")
			.setCreativeTab(CookieMod.cookieTab);
		
		macadamiaLog = new MyModLogs(ConfigLoader.macadamiaLogID)
			.setUnlocalizedName("Macadamia Log")
			.setCreativeTab(CookieMod.cookieTab);
		
		macadamiaLeaf = new MyModLeafs(ConfigLoader.macadamiaLeafID)
			.setUnlocalizedName("Macadamia Leaf");

		//crops
		peanutPlant = new PeanutPlant(ConfigLoader.peanutPlantID).setTextureName("PeanutPlant");
		grapeVine = new GrapeVine(ConfigLoader.grapeVineID).setTextureName("GrapeVine");
		
		//seeds
		grapeSeeds = new GrapeSeeds(ConfigLoader.grapeSeedsID, grapeVine.blockID,
				Block.tilledField.blockID).setUnlocalizedName("Grapes");
		peanutSeeds = new PeanutSeed(ConfigLoader.peanutSeedsID, peanutPlant.blockID,
				Block.tilledField.blockID).setUnlocalizedName("Peanuts");
	}
	
	private static void RegisterItems()
	{
		//Furnace
		GameRegistry.registerBlock(sunTable, "Sun Drying Table");
		GameRegistry.registerBlock(sunTableIdle, "Sun Drying Table Idle");
		
		//Wood
		GameRegistry.registerBlock(pecanLeaf, "PecanLeaf");
		GameRegistry.registerBlock(pecanLog, "PecanLog");
		GameRegistry.registerBlock(pecanSapling, "PecanSapling");
		GameRegistry.registerBlock(macadamiaLeaf, "MacadamiaLeaf");
		GameRegistry.registerBlock(macadamiaLog, "MacadamiaLog");
		GameRegistry.registerBlock(macadamiaSapling, "MacadamiaSapling");
		
		OreDictionary.registerOre("logWood", pecanLog);
		OreDictionary.registerOre("logWood", macadamiaLog);
		
		//Materials
		GameRegistry.registerItem(peanutSeeds, "PecanSeeds", CookieMod.modid);
		GameRegistry.registerItem(grapeSeeds, "GrapeSeeds", CookieMod.modid);
		
		//Crops
		GameRegistry.registerBlock(peanutPlant, "Peanut Plant");
		OreDictionary.registerOre("cropPeanut", peanutPlant);
		
		GameRegistry.registerBlock(grapeVine, "Grape Vine");
		OreDictionary.registerOre("cropGrape", grapeVine);

		//Seeds
		OreDictionary.registerOre("seedPeanut", peanutSeeds);
		OreDictionary.registerOre("seedGrape", grapeSeeds);
	}
	
	private static void AddRecipes()
	{
		//crafting
		GameRegistry.addRecipe(new ItemStack(sunTableIdle,1), "wgw", "gdg", "wgw", 'w', Item.stick, 'g', Item.ingotGold, 'd', Item.diamond);
		GameRegistry.addShapelessRecipe(new ItemStack(cookie_Dough, 4),
				new Object[] { Item.egg, Item.sugar, Item.wheat });
		GameRegistry.addShapelessRecipe(new ItemStack(cc_Dough, 4),
				new Object[] { cookie_Dough,
						new ItemStack(Item.dyePowder, 4, 3) });
		GameRegistry.addShapelessRecipe(new ItemStack(or_Dough, 4),
				new Object[] { cookie_Dough, raisin, oats });
		GameRegistry.addShapelessRecipe(new ItemStack(pb_Dough, 4),
				new Object[] { cookie_Dough, peanutSeeds });
		GameRegistry.addShapelessRecipe(new ItemStack(oats, 2),
				new Object[] { Item.wheat });
		GameRegistry.addShapelessRecipe(new ItemStack(wn_Dough, 4),
				new Object[] { cookie_Dough, pecan});
		GameRegistry.addShapelessRecipe(new ItemStack(wm_Dough, 4),
				new Object[] { cookie_Dough, whiteMacadamia});
		
		//Place Holder for wood
		GameRegistry.addShapelessRecipe(new ItemStack(Block.planks, 4), 
				new Object[]{ pecanLog});
		GameRegistry.addShapelessRecipe(new ItemStack(Block.planks, 4), 
				new Object[]{ macadamiaLog});

		//Smelting
		GameRegistry.addSmelting(cookie_Dough.itemID, new ItemStack(
				Item.cookie, 1), 2F);
		GameRegistry.addSmelting(cc_Dough.itemID, new ItemStack(cc_Cookie, 1),
				2F);
		GameRegistry.addSmelting(or_Dough.itemID, new ItemStack(or_Cookie, 1),
				2F);
		GameRegistry.addSmelting(pb_Dough.itemID, new ItemStack(pb_Cookie, 1),
				2F);
		GameRegistry.addSmelting(wn_Dough.itemID, new ItemStack(wn_Cookie, 1),
				2F);
		GameRegistry.addSmelting(wm_Dough.itemID, new ItemStack(wm_Cookie, 1),
				2F);
		GameRegistry.addSmelting(grapeSeeds.itemID, new ItemStack(raisin, 1),
				1F);
		
		//This is magic :D somehow this is Charcoal. 
		GameRegistry.addSmelting(pecanLog.blockID, new ItemStack(Item.coal, 1, 1),
				0.15F);
		GameRegistry.addSmelting(macadamiaLog.blockID, new ItemStack(Item.coal, 1, 1),
				0.15F);
	}
	
	private static void AddLanguages()
	{
		addStringLocalization("container.SunTable", "Sun Table");
		addStringLocalization("itemGroup.Cookies", "Fresh Cookies");
		//items
		//Using the items name to add for the registry
		LanguageRegistry.addName(pb_Cookie, pb_Cookie.getItemName()); //Cookie is are item type 
		LanguageRegistry.addName(cc_Cookie, cc_Cookie.getItemName()); //My item type has a way to retrieve just the name
		LanguageRegistry.addName(wm_Cookie, wm_Cookie.getItemName()); //Cookie is are item type 
		LanguageRegistry.addName(wn_Cookie, wn_Cookie.getItemName()); //Cookie is are item type 
		LanguageRegistry.addName(or_Cookie, or_Cookie.getItemName()); //Cookie is are item type 
		LanguageRegistry.addName(raisin, raisin.getItemName());
		LanguageRegistry.addName(oats, oats.getItemName());
		LanguageRegistry.addName(pecan, "Pecan");
		LanguageRegistry.addName(whiteMacadamia, whiteMacadamia.getItemName());
		LanguageRegistry.addName(cookie_Dough, cookie_Dough.getItemName());
		LanguageRegistry.addName(cc_Dough, cc_Dough.getItemName());
		LanguageRegistry.addName(or_Dough, or_Dough.getItemName());
		LanguageRegistry.addName(pb_Dough, pb_Dough.getItemName());
		LanguageRegistry.addName(wm_Dough, wm_Dough.getItemName());
		LanguageRegistry.addName(wn_Dough, wn_Dough.getItemName());
		LanguageRegistry.addName(peanutSeeds, "Peanuts");
		LanguageRegistry.addName(grapeSeeds, "Grapes");

		//blocks
		LanguageRegistry.addName(sunTable, "Sun Drying Table");
		LanguageRegistry.addName(sunTableIdle, "Sun Drying Table");
		LanguageRegistry.addName(peanutPlant, "Peanut Plant");
		LanguageRegistry.addName(grapeVine, "Grape Vine");
		LanguageRegistry.addName(pecanLog, "Pecan Log");
		LanguageRegistry.addName(pecanLeaf, "Pecan Leaf");
		LanguageRegistry.addName(pecanSapling, "Pecan Sapling");
		LanguageRegistry.addName(macadamiaLeaf, "Macadamia Leaf");
		LanguageRegistry.addName(macadamiaLog, "Macadamia Log");
		LanguageRegistry.addName(macadamiaSapling, "Macadamia Sapling");
		
		//achievements
		addAchievementLocalization("gettingstarted", "Gettings Started", "Get some supplies to create some cookeis");
		addAchievementLocalization("gettingdirty", "Gettings Your Hands Dirty", "Mix it up, that's it! Use your hands!");
		addAchievementLocalization("powerofthesun", "Power of The Sun", "Dry some grapes with the power of the sun");
		addAchievementLocalization("firstcookie", "First Cookie", "You've got yourself a \"healthy\" snack");
		addAchievementLocalization("raisintheroof", "Raisin The Roof", "Yumm, dry grapes");
	
	}

	private static void addAchievementLocalization(String key, String name, String description)
	{
		LanguageRegistry.instance()
			.addStringLocalization("achievement." + CookieMod.modid + "." + key, name);
		
		LanguageRegistry.instance()
			.addStringLocalization("achievement." + CookieMod.modid + "." + key + ".desc", description);
	}
	
	private static void addStringLocalization(String key, String name)
	{
		LanguageRegistry.instance().addStringLocalization(key, name);
	}
}
