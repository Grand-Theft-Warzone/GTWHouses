package com.grandtheftwarzone.gtwhouses.client.network.handlers;

import com.grandtheftwarzone.gtwhouses.client.network.GTWHouseHandler;
import com.grandtheftwarzone.gtwhouses.client.ui.frames.HouseFrame;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.HousesGUIS2CPacket;
import fr.aym.acsguis.api.ACsGuiApi;

public class HousesGUIHandler implements GTWHouseHandler<HousesGUIS2CPacket> {
    @Override
    public void handle(HousesGUIS2CPacket packet) {
        ACsGuiApi.asyncLoadThenShowGui("gtwhouses:house_gui", () -> new HouseFrame(packet.getHouses()));
    }
}
