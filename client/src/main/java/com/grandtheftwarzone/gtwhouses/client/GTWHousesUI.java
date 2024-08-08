package com.grandtheftwarzone.gtwhouses.client;

import com.grandtheftwarzone.gtwhouses.client.network.NetworkHandler;
import com.grandtheftwarzone.gtwhouses.client.ui.frames.HouseFrame;
import com.grandtheftwarzone.gtwhouses.common.packets.HouseUIPacket;
import fr.aym.acsguis.api.ACsGuiApi;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
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
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkHandler.init();
        MinecraftForge.EVENT_BUS.register(NetworkHandler.class);
    }

    @SubscribeEvent
    public void onPress(InputEvent.KeyInputEvent event) {
        if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
            NetworkHandler.sendToServer(new HouseUIPacket(null));
        } else if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
            NetworkHandler.sendToServer(new IMessage() {
                @Override
                public void fromBytes(ByteBuf buf) {

                }

                @Override
                public void toBytes(ByteBuf buf) {

                }
            });
        }
    }
}
