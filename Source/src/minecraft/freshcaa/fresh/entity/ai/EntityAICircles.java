package freshcaa.fresh.entity.ai;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Vec3;

public class EntityAICircles extends EntityAIBase
{
	private EntityLiving entity;
	private double maxWalkDistance;
	private Vec3 target;
	private double speed;
	
	public EntityAICircles(EntityLiving entity, double speed, double maxWalkDistance)
	{
		this.entity = entity;
		this.speed = speed;
		this.maxWalkDistance = maxWalkDistance;
		this.setMutexBits(12);
	}
	
	@Override
	public boolean shouldExecute()
	{
		if(target == null)
		{
			float randX = this.entity.worldObj.rand.nextFloat();
			float randZ = this.entity.worldObj.rand.nextFloat();
	    	double posX = this.entity.posX + (randX * maxWalkDistance) - (maxWalkDistance * 0.5f);
	    	double posZ = this.entity.posZ + (randZ * maxWalkDistance) - (maxWalkDistance * 0.5f);
	    	target = this.entity.worldObj.getWorldVec3Pool().getVecFromPool(posX, this.entity.posY, posZ);
	    	System.out.println("Set Target: " + String.format("%1$.3f, %2$.3f, %3$.3f", target.xCoord, target.yCoord, target.zCoord));
	    	this.entity.getLookHelper().setLookPosition(posX, this.entity.posY + 2, posZ, 30.0F, 30.0F);
		}
		
		return this.entity.worldObj.isDaytime() && target != null;
	}

	@Override
	public boolean continueExecuting()
    {
        return !this.entity.getNavigator().noPath();
    }
	
    public void resetTask()
    {
    	System.out.println("Task Reset");
        this.target = null;
    }
	
	@Override
    public void startExecuting() 
	{
    	this.entity.getNavigator().tryMoveToXYZ(target.xCoord, target.yCoord, target.zCoord, speed);
    }
}
