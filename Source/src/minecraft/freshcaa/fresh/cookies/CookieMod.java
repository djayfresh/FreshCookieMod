package freshcaa.fresh.cookies;

/*
 * Basic importing
 */
import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import freshcaa.fresh.dough.CC_Dough;
import freshcaa.fresh.dough.CookieDough;
import freshcaa.fresh.dough.OR_Dough;
import freshcaa.fresh.dough.PB_Dough;
import freshcaa.fresh.dough.WM_Dough;
import freshcaa.fresh.dough.WN_Dough;
import freshcaa.fresh.load.ConfigLoader;
import freshcaa.fresh.load.ItemLoader;
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
import freshcaa.minecraft.creativeTabs.CookieTab;
import freshcaa.minecraft.gui.GuiHandler;
import freshcaa.minecraft.item.Cookie;
import freshcaa.minecraft.item.SelfSetFoodItem;
import freshcaa.minecraft.item.SelfSetItem;
import freshcaa.minecraft.tileEntity.TileEntitySunTable;
import freshcaa.minecraft.world.MyBoneMeal_Event;

/*
 * Basic needed forge stuff
 */
@Mod(modid = CookieMod.modid, name = "Fresh Cookies", version = "v1.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class CookieMod
{
	public static final String modid = "FreshCAA";
	
	@Instance(modid)
	public static CookieMod instance;
		
	//GUI's
	public static GuiHandler guiHandler;
	public static final int GUI_SUN_TABLE = 0;
	public static CreativeTabs cookieTab;
	
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ConfigLoader.LoadConfig(event);
	}

	//Declaring Init
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		//Creative Tabs
		cookieTab = new CookieTab("Cookies");
		//Events
		MinecraftForge.EVENT_BUS.register(new MyBoneMeal_Event());
		
		ItemLoader.Load();
		
		AddWorldGen();
		
		//TileEntities
		guiHandler = new GuiHandler();
		GameRegistry.registerTileEntity(TileEntitySunTable.class, "tileEntitySunTable");
	}
	
		
	private static void AddWorldGen()
	{
		GameRegistry.registerWorldGenerator(new WorldGeneratorDjf());
		MinecraftForge.addGrassSeed(new ItemStack(ItemLoader.peanutSeeds), 10);
		MinecraftForge.addGrassSeed(new ItemStack(ItemLoader.grapeSeeds), 10);
		MinecraftForge.addGrassSeed(new ItemStack(ItemLoader.macadamiaSapling), 10);
		MinecraftForge.addGrassSeed(new ItemStack(ItemLoader.pecanSapling), 10);
	}
}