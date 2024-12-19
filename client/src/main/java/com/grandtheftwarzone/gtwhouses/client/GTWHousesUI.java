package com.grandtheftwarzone.gtwhouses.client;

import com.grandtheftwarzone.gtwhouses.client.gtwhouses.network.GTWNetworkHandler;
import com.grandtheftwarzone.gtwhouses.client.gtwhouses.ui.frames.AdminCreateHouseFrame;
import com.grandtheftwarzone.gtwhouses.client.gtwhouses.ui.frames.AdminFrame;
import com.grandtheftwarzone.gtwhouses.client.gtwhouses.ui.frames.HouseFrame;
import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.npc.ShopNPC;
import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.npc.ShopNPCFactory;
import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui.ContainerSell;
import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui.GuiSell;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.SellShopGUIOpenPacket;
import fr.aym.acsguis.api.ACsGuiApi;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.core.Logger;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
@Mod(modid = GTWHousesUI.MODID, version = GTWHousesUI.VERSION, clientSideOnly = true)
public class GTWHousesUI {
    public static final String MODID = "gtwhousesui";
    public static final String VERSION = "1.0";
    @Getter
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = (Logger) event.getModLog();
        MinecraftForge.EVENT_BUS.register(this);

        ACsGuiApi.registerStyleSheetToPreload(HouseFrame.CSS_LOCATION);
        ACsGuiApi.registerStyleSheetToPreload(AdminCreateHouseFrame.CSS_LOCATION);
        ACsGuiApi.registerStyleSheetToPreload(AdminFrame.CSS);

        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "shopnpc"), ShopNPC.class, "shopnpc", ShopNPC.ENTITY_ID, this, 64, 1, false, 0x996600, 0x00ff00);
        RenderingRegistry.registerEntityRenderingHandler(ShopNPC.class, new ShopNPCFactory());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GTWNetworkHandler.init();
        MinecraftForge.EVENT_BUS.register(GTWNetworkHandler.class);
    }

    @SubscribeEvent
    public void onPress(InputEvent.KeyInputEvent event) {


    }
}
