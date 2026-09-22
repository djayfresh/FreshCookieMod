package freshcaa.fresh.entity.ai;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Vec3;

public class EntityAIFindChest extends EntityAIBase
{
	private EntityLiving entity;
	private double maxWalkDistance;
	private double maxWalkDistanceSq;
	private double speed;
	private TileEntityChest target;
	
	public EntityAIFindChest(EntityLiving entity, double speed, double maxWalkDistance)
	{
		this.entity = entity;
		this.speed = speed;
		this.maxWalkDistance = maxWalkDistance;
		this.maxWalkDistanceSq = Math.pow(maxWalkDistance, 2);
		this.setMutexBits(12);
	}
	
	@Override
	public boolean shouldExecute()
	{
		if(target == null)
		{
			List<TileEntity> entities = this.entity.worldObj.loadedTileEntityList;
			double closeChest = Double.MAX_VALUE;
			TileEntityChest closestChest = null;
			for(int i =0; i < entities.size(); i++)
			{
				TileEntity entity = entities.get(i);
				if(entity instanceof TileEntityChest)
				{
					TileEntityChest targetChest = (TileEntityChest) entity;
					if(targetChest != null)
					{
						double distSq = this.entity.getDistanceSq(targetChest.xCoord, targetChest.yCoord, targetChest.zCoord);
						if(distSq < maxWalkDistanceSq)
						{
							if(distSq < closeChest)
							{
								System.out.println("Found a close chest: " + distSq);
								closeChest = distSq;
								closestChest = targetChest;
							}
						}
					}
				}
			}
			if(closestChest != null)
			{
				target = closestChest;
		    	System.out.println("Set Target: " + String.format("%1$d, %2$d, %3$d", target.xCoord, target.yCoord, target.zCoord));
		    	this.entity.getLookHelper().setLookPosition(target.xCoord, this.entity.posY + 2, target.zCoord, 30.0F, 30.0F);
			}
			else
			{
				//System.out.println("Didn't find any chests");
			}
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
    	
    }
	
	@Override
    public void startExecuting() 
	{
		this.entity.getNavigator().tryMoveToXYZ(target.xCoord, target.yCoord, target.zCoord, speed);
    }
}
