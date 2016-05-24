package freshcaa.fresh.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.fresh.load.ItemLoader;
import freshcaa.fresh.tileEntity.TileEntitySunTable;

public class SunTable extends BlockContainer
{
	private Random random = new Random();
	private boolean isActive;
	private static boolean keepInventory;
	
	@SideOnly(Side.CLIENT)
	private Icon iconTop;

	public SunTable(int ID, boolean isActive)
	{
		super(ID, Material.rock);
		setUnlocalizedName(isActive ? "Sun Drying Table"
				: "Sun Drying Table Idle");
		setHardness(3.5F);
		if (!isActive)
			setCreativeTab(CookieMod.cookieTab);
		this.isActive = isActive;
		// TODO Auto-generated constructor stub
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		// TODO Auto-generated method stub
		return new TileEntitySunTable();
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister)
	{
		this.blockIcon = iconRegister.registerIcon(CookieMod.modid + ":SunTable_Side");
		this.iconTop = iconRegister.registerIcon(CookieMod.modid + ":"
				+ (isActive ? "SunTable_Top" : "SunTable_Top_idle"));
	}

	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int metadata)
	{
		// Bottom side = 0
		// Top side = 1
		// facing = side == metadata
		// back = 
		// leftSide = 
		// rightSide = 
		return side == 1 ? iconTop : blockIcon; //first blockicon should be front when holding in hand
	}

	public int idDropped(int par1, Random random, int par3)
	{
		return ItemLoader.sunTableIdle.blockID;
	}

	public void onBlockAdded(World world, int x, int y, int z)
	{
		super.onBlockAdded(world, x, y, z);
		this.setDefaultDirection(world, x, y, z);
		if(!keepInventory)
			updateBlockState(isBlockInSun(world, x, y, z), world, x, y, z);
	}

	/**
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	private void setDefaultDirection(World world, int x, int y, int z)
	{
		if (!world.isRemote)
		{
			int l = world.getBlockId(x, y, z - 1); //behind
			int il = world.getBlockId(x, y, z + 1); //front
			int jl = world.getBlockId(x - 1, y, z); //left
			int kl = world.getBlockId(x + 1, y, z); //right

			byte b0 = 3; // sides 2, 3, 4, 5 
			if (Block.opaqueCubeLookup[l] && !Block.opaqueCubeLookup[il]) //full block on behind, not full block on front
			{
				b0 = 3;
			}

			if (Block.opaqueCubeLookup[il] && !Block.opaqueCubeLookup[l]) //full block on front, not full block on behind
			{
				b0 = 2;
			}

			if (Block.opaqueCubeLookup[jl] && !Block.opaqueCubeLookup[kl]) //full block on left, not full block on right
			{
				b0 = 5;
			}

			if (Block.opaqueCubeLookup[kl] && !Block.opaqueCubeLookup[jl]) //full block on right, not full block on left
			{
				b0 = 4;
			}

			world.setBlockMetadataWithNotify(x, y, z, b0, 2); //what 
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLivingBase entity, ItemStack itemStack)
	{
		int l = MathHelper
				.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3; //Direction

		if (l == 0)
		{
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);
		}

		if (l == 1)
		{
			world.setBlockMetadataWithNotify(x, y, z, 5, 2);
		}

		if (l == 2)
		{
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);
		}

		if (l == 3)
		{
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);
		}

		if (itemStack.hasDisplayName())
		{
			((TileEntitySunTable) world.getBlockTileEntity(x, y, z))
					.setGuiDisplayName(itemStack.getDisplayName());

		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if(!world.isRemote) //Server
		{
			FMLNetworkHandler.openGui(player, CookieMod.instance, CookieMod.GUI_SUN_TABLE, world, x, y, z);
			updateBlockState(isBlockInSun(world, x, y, z), world, x, y, z);
		}
		
		return true;
	}

	private boolean isBlockInSun(World world, int x, int y, int z)
	{
		return world.canBlockSeeTheSky(x, y+2, z) && world.getBlockLightValue(x, y+1, z) >=14;
	}

	public static void updateBlockState(boolean burning, World worldObj,
			int x, int y, int z)
	{
		int metaData = worldObj.getBlockMetadata(x, y, z);
		
		TileEntitySunTable entity = (TileEntitySunTable) worldObj.getBlockTileEntity(x, y, z);
		entity.isInSun = burning;
		keepInventory = true;
		if(burning)
		{
			worldObj.setBlock(x, y, z, ItemLoader.sunTable.blockID);
		}
		else
		{
			worldObj.setBlock(x, y, z, ItemLoader.sunTableIdle.blockID);
		}
		
		keepInventory = false;
		
		worldObj.setBlockMetadataWithNotify(x, y, z, metaData, 2);
		
		if(entity != null)
		{
			entity.validate();
			worldObj.setBlockTileEntity(x, y, z, entity);
		}
	}
	
	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int something)
	{
		return Container.calcRedstoneFromInventory((IInventory)world.getBlockTileEntity(x, y, z));
	}
	
	@Override
	/**
	 * Middle mouse click when in creative mode
	 * gives back the idle block, avoids returning active furnace
	 */
	public int idPicked(World world, int x, int y, int z)
	{
		return ItemLoader.sunTableIdle.blockID;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int oldBlockID, int oldMetaData)
	{
		if(!keepInventory)	
		{
			TileEntitySunTable tileEntity = (TileEntitySunTable) world.getBlockTileEntity(x, y, z);
			if(tileEntity != null)
			{
				for(int i = 0; i < tileEntity.getSizeInventory(); i++)
				{
					ItemStack itemStack = tileEntity.getStackInSlot(i);
					if(itemStack != null)
					{
						float xRan = random.nextFloat() * 0.8F + 0.1F;
						float yRan = random.nextFloat() * 0.8F + 0.1F;
						float zRan = random.nextFloat() * 0.8F + 0.1F;
						
						while(itemStack.stackSize > 0)
						{
							int tenTo30 = random.nextInt(21) + 10;
							
							if(tenTo30 > itemStack.stackSize)
							{
								tenTo30 = itemStack.stackSize;
							}
							
							itemStack.stackSize -= tenTo30;
							
							EntityItem item = new EntityItem(world, (double)((float)x + xRan), (double)((float)y + yRan), (double)((float)z + zRan), new ItemStack(itemStack.itemID, tenTo30, itemStack.getItemDamage()));
							
							if(itemStack.hasTagCompound()) //Stores any aditionaly data
							{
								item.getEntityItem().setTagCompound((NBTTagCompound)itemStack.getTagCompound().copy());
							}
							
							float motion = 0.05f;
							item.motionX = (double)((float)random.nextGaussian() * motion);
							item.motionY = (double)((float)random.nextGaussian() * motion + 0.2f); //it should go up no matter what
							item.motionZ = (double)((float)random.nextGaussian() * motion);
							
							world.spawnEntityInWorld(item);
						}
					}
				}
				
				world.func_96440_m(x, y, z, oldBlockID);
			}
		}
		
		super.breakBlock(world, x, y, z, oldBlockID, oldMetaData);
	}
}
