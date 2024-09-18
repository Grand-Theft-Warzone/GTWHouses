package com.grandtheftwarzone.gtwhouses.client.network.handlers;

import com.grandtheftwarzone.gtwhouses.client.GTWHousesUI;
import com.grandtheftwarzone.gtwhouses.client.network.GTWHouseHandler;
import com.grandtheftwarzone.gtwhouses.client.ui.frames.AdminCreateHouseFrame;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.HouseCoordsS2CPacket;
import fr.aym.acsguis.api.ACsGuiApi;

public class HouseCoordsHandler implements GTWHouseHandler<HouseCoordsS2CPacket> {
    @Override
    public void handle(HouseCoordsS2CPacket message) {
        ACsGuiApi.asyncLoadThenShowGui("gtwhouses:create_house_ui", () -> {
            AdminCreateHouseFrame frame = new AdminCreateHouseFrame();
            frame.loadPacket(message);
            return frame;
        });
    }
}
