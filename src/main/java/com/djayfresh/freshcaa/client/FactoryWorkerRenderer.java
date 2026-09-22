package com.djayfresh.freshcaa.client;

import com.djayfresh.freshcaa.FreshCookies;
import com.djayfresh.freshcaa.entity.FactoryWorker;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.Identifier;

/** Renders the Factory Worker with the vanilla villager model and the mod's own 64x64 skin. */
public class FactoryWorkerRenderer extends MobRenderer<FactoryWorker, VillagerRenderState, VillagerModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(FreshCookies.MODID, "textures/entity/factory_worker.png");

    public FactoryWorkerRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
    }

    @Override
    public Identifier getTextureLocation(VillagerRenderState state) {
        return TEXTURE;
    }

    @Override
    public VillagerRenderState createRenderState() {
        return new VillagerRenderState();
    }
}
