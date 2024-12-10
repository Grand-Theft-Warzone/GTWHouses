package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.npc;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class ShopNPCRenderer extends RenderLiving<ShopNPC> {
    //private static final ResourceLocation TEXTURE = new ResourceLocation("gtwnpcshop", "textures/entity/shop_npc.png");
    //private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/entity/zombie/zombie.png");

    private final ResourceLocation TEXTURE = new ResourceLocation( "textures/entity/shop_npc.png");

    public ShopNPCRenderer(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelBiped(), 0.5f);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(ShopNPC entity) {
        return TEXTURE;
    }
}
