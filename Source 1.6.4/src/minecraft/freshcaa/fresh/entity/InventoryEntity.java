package freshcaa.fresh.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryEntity implements IInventory
{
    public ItemStack[] mainInventory = new ItemStack[9];
    
    protected Entity entity;
    
    public InventoryEntity(Entity entity)
    {
    	this.entity = entity;
    }

	@Override
	public int getSizeInventory()
	{
		// TODO Auto-generated method stub
		return mainInventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int i)
	{
		// TODO Auto-generated method stub
		return mainInventory[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		int stackSize = mainInventory[i].stackSize;
		stackSize = mainInventory[i].stackSize - j;
		if(stackSize < 0)
			stackSize = 0;
		
		mainInventory[i].stackSize = stackSize;
		// TODO Auto-generated method stub
		return mainInventory[i];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack)
	{
		// TODO Auto-generated method stub
		mainInventory[i] = itemstack;
	}

	@Override
	public String getInvName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInvNameLocalized()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onInventoryChanged()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void openChest()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeChest()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		if(i >= mainInventory.length)
			return false;
		
		if(mainInventory[i] == null)
			return false;
		
		if(mainInventory[i] != itemstack)
			return false;
		
		return true;
	}

}
