package freshcaa.fresh.cookies;

/*
 * Basic importing
 */
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import freshcaa.fresh.creativeTabs.CookieTab;
import freshcaa.fresh.events.EventRegister;
import freshcaa.fresh.gui.GuiHandler;
import freshcaa.fresh.load.ConfigLoader;
import freshcaa.fresh.load.ItemLoader;
import freshcaa.fresh.tileEntity.TileEntitySunTable;

/**
 * CookieMod - Landing point for loading FreshCAA into Minecraft. 
 * Used to initalize and provide location for this mods starting points.
 * @author Doug Fresh
 *
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

	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		cookieTab = new CookieTab("Cookies");
		
		EventRegister.Register();
		
		ItemLoader.Load();
		
		AddWorldGen();
		
		//TileEntities
		guiHandler = new GuiHandler();
		GameRegistry.registerTileEntity(TileEntitySunTable.class, "tileEntitySunTable");
	}
			
	private static void AddWorldGen()
	{
		GameRegistry.registerWorldGenerator(new WorldGeneratorDjf());
		MinecraftForge.addGrassSeed(new ItemStack(ItemLoader.peanutSeeds), ConfigLoader.peanutSeedDropWeight);
		MinecraftForge.addGrassSeed(new ItemStack(ItemLoader.grapeSeeds), ConfigLoader.grapeSeedDropWeight);
		MinecraftForge.addGrassSeed(new ItemStack(ItemLoader.macadamiaSapling), ConfigLoader.macadamiaSaplingDropWeight);
		MinecraftForge.addGrassSeed(new ItemStack(ItemLoader.pecanSapling), ConfigLoader.pecanSaplingDropWeight);
	}
}