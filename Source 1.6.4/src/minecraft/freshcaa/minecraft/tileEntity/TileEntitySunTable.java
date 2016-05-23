package freshcaa.minecraft.tileEntity;

import java.util.ArrayList;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameRegistry;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.minecraft.block.SunTable;

public class TileEntitySunTable extends TileEntity implements ISidedInventory
{
	private String localizedName;
	public boolean isInSun = false;
	private static final int[] slots_top = new int[] { 0 };
	private static final int[] slots_sides = new int[] { 1 };
	private static final int[] slots_bottom = new int[] { 1 };
	private ItemStack[] slots = new ItemStack[2]; // 0 - item 1 - fuel 2 - creation

	/**
	 * Furnace speed (normally 200) default 100
	 */
	public int furnaceSpeed = 100;
	///**
	// * How long this furnace will continue to burn for(fuel)
	// */
	//public int burnTime;
	///**
	// * Start time for the current fuel
	// */
	//public int currentItemBurnTime;
	/**
	 * How long before item cooked
	 */
	public int cookTime;

	public int getSizeInventory()
	{
		return slots.length;
	}

	@Override
	public void updateEntity()
	{
		boolean flag = isBurning(); //active /inactive
		isInSun = flag;
		boolean flag1 = false; //change of inventory

		if (!worldObj.isRemote)
		{
			//If we have no fuel
			if (!isBurning() && canSmelt())
			{
				//currentItemBurnTime = getItemBurnTime(slots[1]); //get the burntime from the fuel slot
				if (isBurning())
				{
					//if (slots[1] != null)
					//{
					//	slots[1].stackSize--;
					//	if (slots[1].stackSize == 0)
					//	{
					//		slots[1] = slots[1].getItem()
					//				.getContainerItemStack(slots[1]); //Probably null WTF are we doing it for!
					//	}
					//}
				}
			}

			if (isBurning() && canSmelt())
			{
				cookTime++;

				if (cookTime == furnaceSpeed)
				{
					cookTime = 0;
					smeltItem();
					flag1 = true;
				}
			} else
			{
				cookTime = 0;
			}

			if (flag != isBurning()) //was burning, no longer burning
			{
				flag1 = true;
				SunTable.updateBlockState(isBurning(), worldObj, xCoord,
						yCoord, zCoord); //this should update the actually furnace to inactive/active(true)
			}
		}

		if (flag1)
		{
			onInventoryChanged();
		}
	}

	private void smeltItem()
	{
		if (canSmelt())
		{
			ItemStack itemStack = FurnaceRecipes.smelting().getSmeltingResult(
					slots[0]);

			if (slots[1] == null)
			{
				slots[1] = itemStack.copy(); //Prevents grabbing a bad itemStack, previously modified
			} else if (slots[1].isItemEqual(itemStack))
			{
				slots[1].stackSize += itemStack.stackSize;
			}

			slots[0].stackSize--;
			if (slots[0].stackSize <= 0)
			{
				slots[0] = null;
			}
		}
	}

	public boolean isBurning()
	{
		float lightValue = worldObj.getBlockLightValue(xCoord, yCoord+1, zCoord);
		//System.out.println("BrightnessValue: " + lightValue);
		return worldObj.canBlockSeeTheSky(xCoord, yCoord+2, zCoord) && lightValue >= 14;
	}

	private boolean canSmelt()
	{
		if (slots[0] == null)
		{
			return false;
		} 
		else
		{
			ItemStack itemStack = FurnaceRecipes.smelting().getSmeltingResult(
					slots[0]); //Item that will be smelted too
			int itemOreID = OreDictionary.getOreID(slots[0]);
			int grapeOreID = OreDictionary.getOreID("seedGrape");
			//FMLLog.log("FreshCAA", Level.FINE, slots[0].getUnlocalizedName() + " Ores ID: " + itemOreID);
			//If Null no Recipe excistes or this isn't grapes
			if (itemStack == null || itemOreID != grapeOreID)
			{
				return false;
			}
			if (slots[1] == null)
				return true; //If this is the first item you can smelt
			if (!slots[1].isItemEqual(itemStack))
				return false; //if this item we smelt to not the same as the current smelted item
			int result = slots[1].stackSize + itemStack.stackSize;
			return result <= getInventoryStackLimit()
					&& result <= itemStack.getMaxStackSize();
		}
	}

	public void setGuiDisplayName(String displayName)
	{
		this.localizedName = displayName;
	}

	public boolean isInvintoryNameLocalized()
	{
		return localizedName != null && localizedName.length() > 0;
	}

	public String getInventoryName()
	{
		return isInvintoryNameLocalized() ? localizedName
				: "container.SunTable";
	}

	@Override
	public ItemStack getStackInSlot(int i)
	{
		return slots[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		if (slots[i] != null)
		{
			ItemStack itemStack;
			if (slots[i].stackSize <= j) //Can't split the itemstack
			{
				itemStack = slots[i];
				slots[i] = null;
				return itemStack;
			} else
			{
				itemStack = slots[i].splitStack(j);
				if (slots[i].stackSize == 0)
				{
					slots[i] = null;
				}
				return itemStack;
			}
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
		if (slots[i] != null)
		{
			ItemStack itemStack = slots[i];
			slots[i] = null;
			return itemStack;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack)
	{
		slots[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
		{
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInvName()
	{
		return null;
	}

	@Override
	public boolean isInvNameLocalized()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer)
	{
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this ? false
				: entityplayer.getDistanceSq((double) xCoord + 0.5D,
						(double) yCoord + 0.5D, (double) zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openChest()
	{

	}

	@Override
	public void closeChest()
	{

	}
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		
		//burnTime = nbt.getShort("GenericFurnace_burnTime");
		cookTime = nbt.getShort("GenericFurnace_cookTime");
		
		NBTTagList list = nbt.getTagList("GenericFurnace_slots");
		slots = new ItemStack[getSizeInventory()];
		for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound compond = (NBTTagCompound) list.tagAt(i);
			byte slotIndex = compond.getByte("Slot");
			
			if(slotIndex >= 0 && slotIndex < slots.length)
			{
				slots[slotIndex] = ItemStack.loadItemStackFromNBT(compond);
			}
		}

		//currentItemBurnTime = getItemBurnTime(slots[1]); //Have to call after loading slots back in
		if(nbt.hasKey("GenericFurnace_Name"))
			localizedName = nbt.getString("GenericFurnace_Name");
	}
	
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		
		//nbt.setShort("GenericFurnace_burnTime", (short)burnTime);
		nbt.setShort("GenericFurnace_cookTime", (short)cookTime);
		
		NBTTagList list = new NBTTagList();
		for(int i = 0; i < slots.length; i++)
		{
			if(slots[i] != null)
			{
				NBTTagCompound compound = new NBTTagCompound();
				compound.setByte("Slot", (byte)i);
				slots[i].writeToNBT(compound);
				list.appendTag(compound);
			}
		}
		
		nbt.setTag("GenericFurnace_slots", list);
		
		if(isInvintoryNameLocalized())
		{
			nbt.setString("GenericFurnace_Name", localizedName);
		}
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		return i == 1 ? false : true;
	}

	/**
	 * If the block can see the sun, then everything is fuel
	 * 
	 * @param itemstack
	 * @return
	 */
	public static boolean isItemFuel(ItemStack itemstack)
	{
		return getItemBurnTime(itemstack) > 0;
	}

	public static int getItemBurnTime(ItemStack itemstack)
	{
		if (itemstack == null)
		{
			return 0;
		} else
		{
			Item item = itemstack.getItem();
			int itemID = item.itemID;

			if (item instanceof ItemBlock && Block.blocksList[itemID] != null)
			{
				Block block = Block.blocksList[itemID];

				if (block == Block.woodSingleSlab)
				{
					return 150;
				}
				if (block.blockMaterial == Material.wood)
				{
					return 300;
				}

				if (block == Block.coalBlock)
				{
					return 16000;
				}
			}

			if (item instanceof ItemTool
					&& ((ItemTool) item).getToolMaterialName().equals("WOOD"))
				return 200;
			if (item instanceof ItemSword
					&& ((ItemSword) item).getToolMaterialName().equals("WOOD"))
				return 200;
			if (item instanceof ItemHoe
					&& ((ItemHoe) item).getMaterialName().equals("WOOD"))
				return 200;

			if (itemID == Item.stick.itemID)
				return 100;
			if (itemID == Item.coal.itemID)
				return 1600;
			if (itemID == Item.bucketLava.itemID)
				return 20000;
			if (itemID == Block.sapling.blockID)
				return 100;
			if (itemID == Item.blazeRod.itemID)
				return 2400;

			return GameRegistry.getFuelValue(itemstack);
		}
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1)
	{
		return var1 == 0 ? slots_bottom : (var1 == 1 ? slots_sides : slots_top);
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j)
	{
		return isItemValidForSlot(i, itemstack);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j)
	{
		return j != 1 || itemstack.itemID == Item.bucketEmpty.itemID; //not from the top, and not a bucket
	}

	public int getBurnTimeRemainingScaled(int scaleFactor)
	{
		//if(currentItemBurnTime == 0)
		//{
		//	currentItemBurnTime = furnaceSpeed;
		//}
		return (furnaceSpeed * scaleFactor) / furnaceSpeed;
	}

	public int getCookProgressScaled(int scaleFactor)
	{
		// TODO Auto-generated method stub
		return (cookTime * scaleFactor) / furnaceSpeed;
	}
}