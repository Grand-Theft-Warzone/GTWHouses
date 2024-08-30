package com.grandtheftwarzone.gtwhouses.client;

import com.grandtheftwarzone.gtwhouses.client.network.GTWNetworkHandler;
import com.grandtheftwarzone.gtwhouses.client.ui.frames.AdminCreateHouseFrame;
import com.grandtheftwarzone.gtwhouses.client.ui.frames.AdminHouseFrame;
import com.grandtheftwarzone.gtwhouses.client.ui.frames.HouseFrame;
import com.grandtheftwarzone.gtwhouses.client.ui.panels.MyHousesPanel;
import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.OpenGUIC2SPacket;
import fr.aym.acsguis.api.ACsGuiApi;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
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
        //ACsGuiApi.registerStyleSheetToPreload(AdminHouseFrame.CSS_LOCATION);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GTWNetworkHandler.init();
        MinecraftForge.EVENT_BUS.register(GTWNetworkHandler.class);
    }

    @SubscribeEvent
    public void onPress(InputEvent.KeyInputEvent event) {
        if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
            // NetworkHandler.sendToServer(new HouseUIPacket(null));
            ACsGuiApi.asyncLoadThenShowGui("admin_create_house.css", AdminCreateHouseFrame::new);
            //ACsGuiApi.asyncLoadThenShowGui("my_houses", MyHousesFrame::new);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_I)) {
            GTWNetworkHandler.sendToServer(new OpenGUIC2SPacket(OpenGUIC2SPacket.OpenGUIType.HOUSES));
        } else if (Keyboard.isKeyDown(Keyboard.KEY_U)) {
            ACsGuiApi.asyncLoadThenShowGui("admin_house", AdminHouseFrame::new);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
            GTWNetworkHandler.sendToServer(new IGTWPacket() {
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
