package freshcaa.fresh.entity;

import net.minecraft.client.model.ModelVillager;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;
import freshcaa.fresh.render.FactoryWorkerRender;

public class FactoryWorkerEntity extends CommonEntity
{
	@Override
    public void registerRenderThings() 
    {
        RenderingRegistry.registerEntityRenderingHandler(FactoryWorker.class, new FactoryWorkerRender(new ModelVillager(0.5F), 0.5F));
//the 0.5F is the shadowsize
    }
    
    @Override
    public void registerSound() {
        //MinecraftForge.EVENT_BUS.register(new YourSoundEvent());//register the sound event handling class
    }
}
