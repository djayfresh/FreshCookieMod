package com.fresh.cookies;

/*
 * Basic importing
 */
import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.LanguageRegistry;

import com.fresh.dough.CC_Dough;
import com.fresh.dough.CookieDough;
import com.fresh.dough.OR_Dough;
import com.fresh.dough.PB_Dough;
import com.fresh.dough.WM_Dough;
import com.fresh.dough.WN_Dough;
import com.fresh.materials.GrapeSeeds;
import com.fresh.materials.Oats;
import com.fresh.materials.PeanutSeed;
import com.fresh.materials.Pecan;
import com.fresh.materials.Raisin;
import com.fresh.materials.WhiteMacadamia;
import com.fresh.trees.MyModLeafs;
import com.fresh.trees.MyModLogs;
import com.fresh.trees.MyModSaplings;
import com.minecraft.block.GrapeVine;
import com.minecraft.block.PeanutPlant;
import com.minecraft.block.SunTable;
import com.minecraft.creativeTabs.CookieTab;
import com.minecraft.gui.GuiHandler;
import com.minecraft.item.Cookie;
import com.minecraft.item.SelfSetItem;
import com.minecraft.tileEntity.TileEntitySunTable;
import com.minecraft.world.MyBoneMeal_Event;

/*
 * Basic needed forge stuff
 */
@Mod(modid = CookieMod.modid, name = "Fresh Cookies", version = "v1.0")
//@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class CookieMod
{
	public static CookieMod instance;
	public static final String modid = "FreshCAA";
	
	//ID's for config file
	private static int cc_CookieID;
	private static int wm_CookieID;
	private static int pb_CookieID;
	private static int wn_CookieID;
	private static int or_CookieID;
	private static int pecanID;
	private static int raisinID;
	private static int oatsID;
	private static int whiteMacadamiaID;
	private static int cookie_DoughID;
	private static int cc_DoughID;
	private static int or_DoughID;
	private static int wm_DoughID;
	private static int wn_DoughID;
	private static int pb_DoughID;
	private static int peanutSeedsID;
	private static int grapeSeedsID;
	private static int peanutPlantID;
	private static int grapeVineID;
	private static int sunTableID;
	private static int sunTableIdleID;
	private static int pecanLogID;
	private static int pecanLeafID;
	private static int pecanSaplingID;
	private static int macadamiaLogID;
	private static int macadamiaLeafID;
	private static int macadamiaSaplingID;
	
	
	/*
	 * ToolMaterial
	 */
	//Telling forge that we are creating these
	//items
	public static Cookie cc_Cookie; //My item type that defines its own name and tab
	public static Cookie wm_Cookie;
	public static Cookie pb_Cookie;
	public static Cookie wn_Cookie;
	public static Cookie or_Cookie;
	public static Item pecan;
	public static SelfSetItem raisin;
	public static SelfSetItem oats;
	public static SelfSetItem whiteMacadamia;
	public static SelfSetItem cookie_Dough;
	public static SelfSetItem cc_Dough;
	public static SelfSetItem or_Dough;
	public static SelfSetItem pb_Dough;
	public static SelfSetItem wm_Dough;
	public static SelfSetItem wn_Dough;
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
	
	
	//tools
	
	//GUI's
	public static GuiHandler guiHandler;
	public static final int GUI_SUN_TABLE = 0;
	public static CreativeTabs cookieTab;
	
	/**
	 * loads all ID's from FreshCAA.cfg file
	 * @param event
	 */
	private void initConfig(FMLInitializationEvent event)
	{
		instance = this;
		Configuration config = new Configuration(new File("config/FreshCAA.cfg"));
		config.load();
		
		
		cc_CookieID = config.get("Item", "Chocolate Chip Cookie", 2100).getInt();
		wm_CookieID = config.get("Item", "Pecan Cookie", 2101).getInt();
		pb_CookieID = config.get("Item", "Peanut Butter Cookie", 2102).getInt();
		wn_CookieID = config.get("ITem", "White Macadamia Cookie", 2103).getInt();
		or_CookieID = config.get("Item", "Oatmeal Raisin Cookie", 2104).getInt();
		pecanID = config.get("Material", "Pecan", 2105).getInt();
		raisinID = config.get("Material", "Raisin", 2106).getInt();
		oatsID = config.get("Material", "Oats", 2107).getInt();
		whiteMacadamiaID = config.get("Material", "White Macadamia", 2108).getInt();
		cookie_DoughID = config.get("Item", "Cookie Dough", 2109).getInt();
		cc_DoughID = config.get("Item", "Chocolate Chip Dough", 2110).getInt();
		or_DoughID = config.get("Item", "Oatmeal Raisin Dough", 2111).getInt();
		wm_DoughID = config.get("Item", "White Macadamia Dough", 2112).getInt();
		wn_DoughID = config.get("Item", "Pecan Dough", 2113).getInt();
		pb_DoughID = config.get("Item", "Peanut Butter Dough", 2114).getInt();
		peanutSeedsID = config.get("ITem", "Peanuts", 2115).getInt();
		grapeSeedsID = config.get("Item", "Grapes", 2116).getInt();
		peanutPlantID = config.get("Block", "Peanut Plant", 2117).getInt();
		grapeVineID = config.get("Block", "Grape Vines", 2118).getInt();
		sunTableID = config.get("Block", "Sun Drying Table", 2119).getInt();
		sunTableIdleID = config.get("Block", "Sun Drying Table Idle", 2120).getInt();
		pecanLogID = config.get("Block", "Pecan Log", 2121).getInt();
		pecanLeafID = config.get("Block", "Pecan Leaf", 2122).getInt();
		pecanSaplingID = config.get("Block", "Pecan Sapling", 2123).getInt();
		macadamiaLogID = config.get("Block", "Macadamia Log", 2124).getInt();
		macadamiaLeafID = config.get("Block", "Macadamia Leaf", 2125).getInt();
		macadamiaSaplingID = config.get("Block", "Macadamia Sapling", 2126).getInt();
		
		config.save();
	}

	//Declaring Init
	@EventHandler
	public void preinit(FMLInitializationEvent event)
	{
		//Load config file
		initConfig(event);
		//Creative Tabs
		cookieTab = new CookieTab("Cookies");
		//Events
		MinecraftForge.EVENT_BUS.register(new MyBoneMeal_Event());
		
		// define items
		//Cookies
		cc_Cookie = new Cookie_CC(cc_CookieID); //Cookie is are item type 
		wm_Cookie = new Cookie_WM(wm_CookieID); //Cookie is are item type 
		wn_Cookie = new Cookie_WN(wn_CookieID); //Cookie is are item type 
		or_Cookie = new Cookie_OR(or_CookieID); //Cookie is are item type 
		pb_Cookie = new Cookie_PB(pb_CookieID); //.setUnlocalizedName("Peanut Butter Cookie"); //Cookie is are item type
		//Cookie Materials
		raisin = new Raisin(raisinID);
		oats = new Oats(oatsID);
		pecan = new Pecan(pecanID);
		whiteMacadamia = new WhiteMacadamia(whiteMacadamiaID);
		//Cookie Dough
		cookie_Dough = new CookieDough(cookie_DoughID);
		cc_Dough = new CC_Dough(cc_DoughID);
		or_Dough = new OR_Dough(or_DoughID);
		pb_Dough = new PB_Dough(pb_DoughID); //Peanut Seeds(29) and Plant(28) 
		wm_Dough = new WM_Dough(wm_DoughID); //Peanut Seeds(29) and Plant(28) 
		wn_Dough = new WN_Dough(wn_DoughID); //Peanut Seeds(29) and Plant(28) 
		
		// define blocks
		sunTable = new SunTable(sunTableID, true);
		sunTableIdle = new SunTable(sunTableIdleID, false);
		GameRegistry.registerBlock(sunTable, "Sun Drying Table");
		GameRegistry.registerBlock(sunTableIdle, "Sun Drying Table Idle");
		
		pecanSapling = new MyModSaplings(pecanSaplingID).setUnlocalizedName("Pecan Sapling").setCreativeTab(CookieMod.cookieTab);
		pecanLog = new MyModLogs(pecanLogID).setUnlocalizedName("Pecan Log").setCreativeTab(CookieMod.cookieTab);
		pecanLeaf = new MyModLeafs(pecanLeafID).setUnlocalizedName("Pecan Leaf");
		
		macadamiaSapling = new MyModSaplings(macadamiaSaplingID).setUnlocalizedName("Macadamia Sapling").setCreativeTab(CookieMod.cookieTab);
		macadamiaLog = new MyModLogs(macadamiaLogID).setUnlocalizedName("Macadamia Log").setCreativeTab(CookieMod.cookieTab);
		macadamiaLeaf = new MyModLeafs(macadamiaLeafID).setUnlocalizedName("Macadamia Leaf");
		
		GameRegistry.registerBlock(pecanLeaf, "PecanLeaf");
		GameRegistry.registerBlock(pecanLog, "PecanLog");
		GameRegistry.registerBlock(pecanSapling, "PecanSapling");
		GameRegistry.registerBlock(macadamiaLeaf, "MacadamiaLeaf");
		GameRegistry.registerBlock(macadamiaLog, "MacadamiaLog");
		GameRegistry.registerBlock(macadamiaSapling, "MacadamiaSapling");
		
		//Crops
		peanutPlant = new PeanutPlant(peanutPlantID);
		GameRegistry.registerBlock(peanutPlant, "Peanut Plant");
		grapeVine = new GrapeVine(grapeVineID);
		GameRegistry.registerBlock(grapeVine, "Grape Vine");

		//Seeds
		peanutSeeds = new PeanutSeed(peanutPlant,
				Blocks.farmland);
		grapeSeeds = new GrapeSeeds(grapeVine,
				Blocks.farmland);
		//adding names
		
		LanguageRegistry.instance().addStringLocalization("container.SunTable", "Sun Table");
		LanguageRegistry.instance().addStringLocalization("itemGroup.Cookies", "en_US", "Fresh Cookies");
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
		//crafting
		
		GameRegistry.addRecipe(new ItemStack(sunTableIdle,1), "wsw", "sgs", "wsw", 'w', Blocks.planks, 's', Items.stick, 'g', Blocks.glass);
		GameRegistry.addShapelessRecipe(new ItemStack(cookie_Dough, 4),
				new Object[] { Items.egg, Items.sugar, Items.wheat });
		GameRegistry.addShapelessRecipe(new ItemStack(cc_Dough, 4),
				new Object[] { cookie_Dough,
						new ItemStack(Items.dye, 4, 3) });
		GameRegistry.addShapelessRecipe(new ItemStack(or_Dough, 4),
				new Object[] { cookie_Dough, raisin, oats });
		GameRegistry.addShapelessRecipe(new ItemStack(pb_Dough, 4),
				new Object[] { cookie_Dough, peanutSeeds });
		GameRegistry.addShapelessRecipe(new ItemStack(oats, 2),
				new Object[] { Items.wheat });
		GameRegistry.addShapelessRecipe(new ItemStack(wn_Dough, 4),
				new Object[] { cookie_Dough, pecan});
		GameRegistry.addShapelessRecipe(new ItemStack(wm_Dough, 4),
				new Object[] { cookie_Dough, whiteMacadamia});

		//Smelting
		GameRegistry.addSmelting(cookie_Dough, new ItemStack(
				Items.cookie, 1), 2F);
		GameRegistry.addSmelting(cc_Dough, new ItemStack(cc_Cookie, 1),
				2F);
		GameRegistry.addSmelting(or_Dough, new ItemStack(or_Cookie, 1),
				2F);
		GameRegistry.addSmelting(pb_Dough, new ItemStack(pb_Cookie, 1),
				2F);
		GameRegistry.addSmelting(wn_Dough, new ItemStack(wn_Cookie, 1),
				2F);
		GameRegistry.addSmelting(wm_Dough, new ItemStack(wm_Cookie, 1),
				2F);
		GameRegistry.addSmelting(grapeSeeds, new ItemStack(raisin, 1),
				1F);

		//World Generation
		//GameRegistry.registerWorldGenerator(new WorldGeneratorDjf(), 0);
		MinecraftForge.addGrassSeed(new ItemStack(peanutSeeds), 10);
		MinecraftForge.addGrassSeed(new ItemStack(grapeSeeds), 10);
		MinecraftForge.addGrassSeed(new ItemStack(macadamiaSapling), 10);
		MinecraftForge.addGrassSeed(new ItemStack(pecanSapling), 10);
		
		//TileEntities
		guiHandler = new GuiHandler();
		GameRegistry.registerTileEntity(TileEntitySunTable.class, "tileEntitySunTable");
	}
}