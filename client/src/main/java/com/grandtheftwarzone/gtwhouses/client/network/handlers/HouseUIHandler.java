package com.grandtheftwarzone.gtwhouses.client.network.handlers;

import com.grandtheftwarzone.gtwhouses.client.network.GTWHouseHandler;
import com.grandtheftwarzone.gtwhouses.common.packets.HouseUIPacket;
import fr.aym.acsguis.api.ACsGuiApi;
import net.minecraft.client.Minecraft;
import com.grandtheftwarzone.gtwhouses.client.ui.frames.HouseFrame;

public class HouseUIHandler implements GTWHouseHandler<HouseUIPacket> {
    @Override
    public void handle(HouseUIPacket message) {
        Minecraft.getMinecraft().addScheduledTask(() ->
                ACsGuiApi.asyncLoadThenShowGui("gtwhousesui:house_frame", HouseFrame::new)
        );
    }
}
