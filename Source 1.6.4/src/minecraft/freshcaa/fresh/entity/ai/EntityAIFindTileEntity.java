package freshcaa.fresh.entity.ai;

import java.lang.reflect.Type;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Vec3;

public class EntityAIFindTileEntity extends EntityAIBase
{
	private EntityLiving entity;
	private double maxWalkDistance;
	private double maxWalkDistanceSq;
	private double speed;
	private TileEntity target;
	private Class<? extends TileEntity> classType;
	
	public EntityAIFindTileEntity(EntityLiving entity, Class<? extends TileEntity> toFindType, double speed, double maxWalkDistance)
	{
		this.entity = entity;
		this.speed = speed;
		this.maxWalkDistance = maxWalkDistance;
		this.maxWalkDistanceSq = Math.pow(maxWalkDistance, 2);
		this.setMutexBits(12);
		this.classType = toFindType;
	}
	
	@Override
	public boolean shouldExecute()
	{
		if(target == null)
		{
			List<TileEntity> entities = this.entity.worldObj.loadedTileEntityList;
			double closeEntityDistance = Double.MAX_VALUE;
			TileEntity closestEntity = null;
			for(int i =0; i < entities.size(); i++)
			{
				TileEntity entity = entities.get(i);
				if(classType.isInstance(entity))
				{
					TileEntity targetEntity = classType.cast(entity);
					if(targetEntity != null)
					{
						double distSq = this.entity.getDistanceSq(targetEntity.xCoord, targetEntity.yCoord, targetEntity.zCoord);
						if(distSq < maxWalkDistanceSq)
						{
							if(distSq < closeEntityDistance)
							{
								//System.out.println("Found a close " + classType.getName() + ": " + distSq);
								closeEntityDistance = distSq;
								closestEntity = targetEntity;
							}
						}
					}
				}
			}
			if(closestEntity != null)
			{
				target = closestEntity;
		    	//System.out.println("Set Target: " + String.format("%1$d, %2$d, %3$d", target.xCoord, target.yCoord, target.zCoord));
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
