package freshcaa.fresh.entity;

import freshcaa.fresh.cookies.CookieMod;
import freshcaa.fresh.entity.ai.EntityAICircles;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowGolem;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookAtTradePlayer;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIPlay;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

public class FactoryWorker extends EntityAgeable implements IMerchant, INpc
{
	public static String NAME = "factoryworker";
	protected int randomTickDivider;
	
    public FactoryWorker(World par1World)
    {
        super(par1World);
        this.setSize(0.9F, 1.3F);
        this.getNavigator().setAvoidsWater(true);
        this.getNavigator().setBreakDoors(true);
        this.tasks.addTask(0, new EntityAICircles(this, SharedMonsterAttributes.movementSpeed.getDefaultValue(), 20.0f));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.20000000298023224D);
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
		
	}

	@Override
	public EntityPlayer getCustomer()
	{
		return null;
	}

	@Override
	public MerchantRecipeList getRecipes(EntityPlayer entityplayer)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRecipes(MerchantRecipeList merchantrecipelist)
	{
		// TODO Auto-generated method stub
		
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
}
