package freshcaa.fresh.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.LanguageRegistry;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.fresh.entity.ai.EntityAICircles;
import freshcaa.fresh.entity.ai.EntityAIFindTileEntity;
import freshcaa.fresh.load.ItemLoader;

public class FactoryWorker extends EntityAgeable implements IMerchant, INpc
{
	public static String NAME = "factoryworker";
	protected double movementSpeed;
	protected int randomTickDivider;
	
	public EntityTask[] assignableTasks;
	protected EntityPlayer player;
	protected int activeTask;
		
    public FactoryWorker(World par1World)
    {
        super(par1World);
        this.setSize(0.9F, 1.3F);
        this.getNavigator().setAvoidsWater(true);
        this.getNavigator().setBreakDoors(true);
        this.movementSpeed = SharedMonsterAttributes.movementSpeed.getDefaultValue()/2;
        
        this.assignableTasks = new EntityTask[3];
        
        this.activeTask = 0;
        addTask(0, 0, new EntityAIWander(this, this.movementSpeed), true);
        addTask(1, 1, new EntityAIFindTileEntity(this, TileEntityChest.class, this.movementSpeed, 20.0D));
        addTask(2, 1, new EntityAICircles(this, this.movementSpeed, 20.0f));
        
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(this.movementSpeed);
    }

    @Override
    protected String getLivingSound()
    {
        return CookieMod.modid + ":" + NAME + ".living";//this refers to:yourmod/sound/YourSound
    }

    @Override
    protected String getHurtSound()
    {
        return CookieMod.modid + ":" + NAME + ".hurt";//this refers to:yourmod/sound/optionalFile/YourSound
    }

    @Override
    protected String getDeathSound()
    {
        return CookieMod.modid + ":" + NAME + ".death";//etc.
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.4F;
    }

	@Override
	public EntityAgeable createChild(EntityAgeable entityageable)
	{
		return null;
	}

	@Override
	public void setCustomer(EntityPlayer entityplayer)
	{
		this.player = entityplayer;
	}

	@Override
	public EntityPlayer getCustomer()
	{
		return this.player;
	}

	@Override
	public MerchantRecipeList getRecipes(EntityPlayer entityplayer)
	{
		MerchantRecipeList list = new MerchantRecipeList();
		list.addToListWithCheck(new MerchantRecipe(new ItemStack(Item.diamond, 6), ItemLoader.grapeSeeds));
		// TODO Auto-generated method stub
		return list;
	}

	@Override
	public void setRecipes(MerchantRecipeList merchantrecipelist)
	{
		// TODO Auto-generated method stub
	}
	
	public void addTask(int taskId, int priority, EntityAIBase task)
	{
		addTask(taskId, priority, task, false);
	}
	
	public void addTask(int taskId, int priority, EntityAIBase task, boolean active)
	{
		this.assignableTasks[taskId] = new EntityTask(priority,task);
		if(active){
			this.tasks.addTask(priority, this.assignableTasks[taskId].task);
		}
	}
	
	public void setActiveTask(int taskId)
	{
		EntityTask task = this.assignableTasks[taskId];
		this.tasks.addTask(task.priority, task.task);
	}
	
	public void clearTasks()
	{
		this.tasks.taskEntries.clear();
	}
	
	public void removeTask(int taskId)
	{
		this.tasks.removeTask(this.assignableTasks[taskId].task);
	}

	@Override
	public void useRecipe(MerchantRecipe merchantrecipe)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void func_110297_a_(ItemStack itemstack)
	{
		// TODO Auto-generated method stub
		
	}
	
	/**
     * Returns true if the newer Entity AI code should be run
     */
    protected boolean isAIEnabled()
    {
        return true;
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    protected boolean canDespawn()
    {
        return false;
    }
    protected void updateAITick()
    {
        if (--this.randomTickDivider <= 0)
        {
        	randomTickDivider = 80 + getRNG().nextInt(50);
        }
    }
    
    public String getLocalizedName()
    {
    	return LanguageRegistry.instance().getStringLocalization("entity.factoryworker.name");
    }
    
    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        ItemStack itemstack = par1EntityPlayer.inventory.getCurrentItem();
        boolean flag = itemstack != null && itemstack.itemID == Item.monsterPlacer.itemID;

        if (!flag && this.isEntityAlive() && !this.isChild() && !par1EntityPlayer.isSneaking())
        {
            if (!this.worldObj.isRemote)
            {
                this.setCustomer(par1EntityPlayer);
                par1EntityPlayer.displayGUIMerchant(this, this.getLocalizedName());
            }

            return true;
        }
        else
        {
            return super.interact(par1EntityPlayer);
        }
    }
    
    private class EntityTask
    {
    	public EntityAIBase task;
    	public int priority;
    	
    	public EntityTask(int priority, EntityAIBase task)
    	{
    		this.priority = priority;
    		this.task = task;
    	}
    }
}
