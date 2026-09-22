package com.minecraft.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import com.fresh.cookies.CookieMod;
import com.minecraft.container.ContainerSunTable;
import com.minecraft.tileEntity.TileEntitySunTable;

public class GuiHandler implements IGuiHandler
{
	
	public GuiHandler()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(CookieMod.instance, this);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) //Container
	{
		TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
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
		TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
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
