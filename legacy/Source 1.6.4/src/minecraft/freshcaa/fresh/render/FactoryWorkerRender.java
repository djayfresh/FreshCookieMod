package freshcaa.fresh.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import freshcaa.fresh.cookies.CookieMod;

@SideOnly(Side.CLIENT)
public class FactoryWorkerRender extends RenderLiving
{
	private static final ResourceLocation Your_Texture = new ResourceLocation(CookieMod.modid + ":textures/entity/factoryworker.png");  //refers to:assets/yourmod/textures/entity/yourtexture.png

	public FactoryWorkerRender(ModelBase par1ModelBase, float par2)
    {
        super(par1ModelBase, par2);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return Your_Texture;
    }
}
