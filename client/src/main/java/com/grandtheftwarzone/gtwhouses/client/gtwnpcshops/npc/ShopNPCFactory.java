package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.npc;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class ShopNPCFactory implements IRenderFactory<ShopNPC> {
    @Override
    public Render createRenderFor(RenderManager manager) {
        return new ShopNPCRenderer(manager);
    }
}
