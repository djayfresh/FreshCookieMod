package freshcaa.fresh.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import freshcaa.fresh.container.ContainerSunTable;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.fresh.tileEntity.TileEntitySunTable;

public class GuiHandler implements IGuiHandler
{
	
	public GuiHandler()
	{
		NetworkRegistry.instance().registerGuiHandler(CookieMod.instance, this);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) //Container
	{
		TileEntity entity = world.getBlockTileEntity(x, y, z);
		if(entity != null && ID == CookieMod.GUI_SUN_TABLE)
		{
			switch(ID)
			{
			case CookieMod.GUI_SUN_TABLE:
				if(entity instanceof TileEntitySunTable)
					return new ContainerSunTable(player.inventory, (TileEntitySunTable) entity);
				break;
			default:
				break;
			}
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) //GUI and Container
	{
		TileEntity entity = world.getBlockTileEntity(x, y, z);
		if(entity != null && ID == CookieMod.GUI_SUN_TABLE)
		{
			switch(ID)
			{
			case CookieMod.GUI_SUN_TABLE:
				if(entity instanceof TileEntitySunTable)
					return new GuiSunTable(player.inventory, (TileEntitySunTable) entity);
				break;
			default:
				break;
			}
		}
		
		return null;
	}

}
