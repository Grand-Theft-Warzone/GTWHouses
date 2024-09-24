package com.grandtheftwarzone.gtwhouses.client.network.handlers;

import com.grandtheftwarzone.gtwhouses.client.network.GTWHouseHandler;
import com.grandtheftwarzone.gtwhouses.client.ui.frames.AdminFrame;
import com.grandtheftwarzone.gtwhouses.client.ui.frames.HouseFrame;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.HousesGUIS2CPacket;
import fr.aym.acsguis.api.ACsGuiApi;

import static com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.OpenGUIC2SPacket.OpenGUIType.ADMIN_PANEL;
import static com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.OpenGUIC2SPacket.OpenGUIType.HOUSES;

public class HousesGUIHandler implements GTWHouseHandler<HousesGUIS2CPacket> {
    @Override
    public void handle(HousesGUIS2CPacket packet) {
        switch (packet.getType()) {
            case HOUSES:
                ACsGuiApi.asyncLoadThenShowGui("gtwhouses:house_gui", () -> new HouseFrame(packet.getHouses()));
                break;
            case ADMIN_PANEL:
                ACsGuiApi.asyncLoadThenShowGui("gtwhouses:admin_frame", () -> new AdminFrame(packet.getHouses()));
                break;
        }
    }
}
