package freshcaa.minecraft.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import freshcaa.minecraft.tileEntity.TileEntitySunTable;

public class ContainerSunTable extends Container
{
	private TileEntitySunTable tileEntity;
	private InventoryPlayer playerInventory;
	/**
	 * How long this furnace will continue to burn for(fuel)
	 */
	public int lastBurnTime;
	/**
	 * Start time for the current fuel
	 */
	public int lastItemBurnTime;
	/**
	 * How long before item cooked
	 */
	public int lastCookTime;

	public ContainerSunTable(InventoryPlayer inventoryPlayer,
			TileEntitySunTable entity)
	{
		playerInventory = inventoryPlayer;
		tileEntity = entity;

		addSlotToContainer(new Slot(tileEntity, 0, 56, 17)); //Item to smelt
		//addSlotToContainer(new Slot(tileEntity, 1, 56, 53)); //FUEL SLOT
		addSlotToContainer(new SlotFurnace(inventoryPlayer.player, tileEntity,
				1, 116, 35)); //Output item
		int slotCount = 2;
		int x = 8;
		int y = 84;
		int offset = 18;
		for (int i = 0; i < 3; i++)
		{
			for (int r = 0; r < 9; r++)
			{
				addSlotToContainer(new Slot(inventoryPlayer, r + i * 9 + 9, x
						+ (offset * r), y + (offset * i))); //Accounting for the slotcount bellow 
			}
		}
		x = 8;
		y = 142;
		for (int i = 0; i < 9; i++) //Bottom slots of the container UI
		{
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + (offset * i), y));
		}
	}

	public void addCraftingToCrafters(ICrafting icrafting)
	{
		super.addCraftingToCrafters(icrafting);
		icrafting.sendProgressBarUpdate(this, 0, tileEntity.cookTime);
		//icrafting.sendProgressBarUpdate(this, 1, tileEntity.burnTime);
		//icrafting.sendProgressBarUpdate(this, 1, tileEntity.currentItemBurnTime);
	}

	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++)
		{
			ICrafting icrafting = (ICrafting) crafters.get(i);

			if (lastCookTime != tileEntity.cookTime)
			{
				icrafting.sendProgressBarUpdate(this, 0, tileEntity.cookTime);
			}
			
			//if (lastItemBurnTime != tileEntity.currentItemBurnTime)
			//{
				//icrafting.sendProgressBarUpdate(this, 1,
				//		tileEntity.currentItemBurnTime);
			//}
		}
		
		lastCookTime = tileEntity.cookTime;
	}

	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int slot, int updateValue)
	{
		if (slot == 0) //Item to cook
		{
			tileEntity.cookTime = updateValue;
		}

		//if (slot == 2) //Fuel
		//{
		//	tileEntity.burnTime = updateValue;
		//}

		if (slot == 1) //Smelted item
		{
			//tileEntity.currentItemBurnTime = updateValue;
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) //Shift+click a slot
	{
		ItemStack itemStack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);
		if (slot != null && slot.getHasStack())
		{
			ItemStack itemStack1 = slot.getStack();
			itemStack = itemStack1.copy();

			if (slotIndex == 1)
			{
				if (!mergeItemStack(itemStack1, 2, 38, true)) //Inventory slots 0-2 is the furnace. 3-37(exclusive)
				{
					return null;
				}

				slot.onSlotChange(itemStack1, itemStack);
			} else if (slotIndex != 0)
			{
				if (FurnaceRecipes.smelting().getSmeltingResult(itemStack1) != null)
				{
					if (!mergeItemStack(itemStack1, 0, 1, false))
					{
						return null;
					}
				} else if (slotIndex >= 2 && slotIndex < 30)
				{
					if (!mergeItemStack(itemStack1, 30, 38, false))
					{
						return null;
					}
				} else if (slotIndex >= 30 && slotIndex < 38)
				{
					if (!mergeItemStack(itemStack1, 2, 30, false))
					{
						return null;
					}
				}
			} else if (!mergeItemStack(itemStack1, 2, 38, false))
			{
				return null;
			}

			if (itemStack1.stackSize == 0) //Remove if nothing left
			{
				slot.putStack(null);
			} else
			{
				slot.onSlotChanged();
			}

			if (itemStack1.stackSize == itemStack.stackSize) //Murged and both equal 
			{
				return null;
			}

			slot.onPickupFromSlot(player, itemStack1);
		}

		return itemStack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer)
	{
		return tileEntity.isUseableByPlayer(entityplayer);
	}

}
