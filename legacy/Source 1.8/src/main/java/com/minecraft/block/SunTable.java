package com.minecraft.block;

import java.util.Random;

import javax.swing.Icon;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.fresh.cookies.CookieMod;
import com.minecraft.tileEntity.TileEntitySunTable;

public class SunTable extends BlockContainer
{
	private Random random = new Random();
	private boolean isActive;
	private static boolean keepInventory;
	
	@SideOnly(Side.CLIENT)
	private Icon iconTop;

	public SunTable(int ID, boolean isActive)
	{
		super(Material.rock);
		setUnlocalizedName(isActive ? "Sun Drying Table"
				: "Sun Drying Table Idle");
		setHardness(3.5F);
		if (!isActive)
			setCreativeTab(CookieMod.cookieTab);
		this.isActive = isActive;
		// TODO Auto-generated constructor stub
	}

	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		// TODO Auto-generated method stub
		return new TileEntitySunTable();
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons()
	{
		//this.blockIcon = iconRegister.registerIcon(CookieMod.modid + ":SunTable_Side");
		//this.iconTop = iconRegister.registerIcon(CookieMod.modid + ":"
		//		+ (isActive ? "SunTable_Top" : "SunTable_Top_idle"));
	}
	
	public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam)
    {
        super.onBlockEventReceived(worldIn, pos, state, eventID, eventParam);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
    }

	public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(CookieMod.sunTable);
    }

	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.setDefaultFacing(worldIn, pos, state);
		if(!keepInventory)
			setState(isBlockInSun(worldIn, pos), worldIn, pos);
	}

	private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            Block block = worldIn.getBlockState(pos.north()).getBlock();
            Block block1 = worldIn.getBlockState(pos.south()).getBlock();
            Block block2 = worldIn.getBlockState(pos.west()).getBlock();
            Block block3 = worldIn.getBlockState(pos.east()).getBlock();
            EnumFacing enumfacing = (EnumFacing)state.getValue(BlockFurnace.FACING);

            if (enumfacing == EnumFacing.NORTH && block.isFullBlock() && !block1.isFullBlock())
            {
                enumfacing = EnumFacing.SOUTH;
            }
            else if (enumfacing == EnumFacing.SOUTH && block1.isFullBlock() && !block.isFullBlock())
            {
                enumfacing = EnumFacing.NORTH;
            }
            else if (enumfacing == EnumFacing.WEST && block2.isFullBlock() && !block3.isFullBlock())
            {
                enumfacing = EnumFacing.EAST;
            }
            else if (enumfacing == EnumFacing.EAST && block3.isFullBlock() && !block2.isFullBlock())
            {
                enumfacing = EnumFacing.WEST;
            }

            worldIn.setBlockState(pos, state.withProperty(BlockFurnace.FACING, enumfacing), 2);
        }
    }

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntitySunTable)
            {
                playerIn.displayGUIChest((TileEntitySunTable)tileentity);
    			setState(isBlockInSun(worldIn, pos), worldIn, pos);
        		
            }

            return true;
        }
    }

	private boolean isBlockInSun(World world, BlockPos pos)
	{
		return world.canBlockSeeSky(pos) && world.getBlockLightOpacity(pos) >=14;
	}

	public static void setState(boolean active, World worldObj, BlockPos pos)
	{
		
		TileEntitySunTable entity = (TileEntitySunTable) worldObj.getTileEntity(pos);
		entity.isInSun = active;
		keepInventory = true;
		if(active)
		{
			//worldObj.setBlockState(pos, CookieMod.sunTable);
		}
		else
		{
			//worldObj.setBlockState(pos, CookieMod.sunTableIdle);
		}
		
		keepInventory = false;
		
		//worldObj.setBlockMetadataWithNotify(x, y, z, metaData, 2);
		
		if(entity != null)
		{
			entity.validate();
			worldObj.setTileEntity(pos, entity);
		}
	}
	
	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}
	
	@Override 
	public int getComparatorInputOverride(World worldIn, BlockPos pos)
    {
		return Container.calcRedstoneFromInventory((IInventory)worldIn.getTileEntity(pos));
    }
	
	@Override
	/**
	 * Middle mouse click when in creative mode
	 * gives back the idle block, avoids returning active furnace
	 */
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos)
    {
        Item item = getItem(world, pos);

        if (item == null)
        {
            return null;
        }

        Block block = item instanceof ItemBlock && !isFlowerPot() ? Block.getBlockFromItem(item) : this;
        return new ItemStack(item, 1, block.getDamageValue(world, pos));
    }
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }
}
