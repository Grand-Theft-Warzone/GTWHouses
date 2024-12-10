package com.grandtheftwarzone.gtwhouses.client.gtwhouses.network.handlers;


import com.grandtheftwarzone.gtwhouses.client.gtwhouses.network.GTWHouseHandler;
import com.grandtheftwarzone.gtwhouses.client.gtwhouses.ui.frames.AdminCreateHouseFrame;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.RegisterHouseS2CPacket;
import fr.aym.acsguis.api.ACsGuiApi;

public class RegisterHouseHandler implements GTWHouseHandler<RegisterHouseS2CPacket> {
    @Override
    public void handle(RegisterHouseS2CPacket message) {
        ACsGuiApi.asyncLoadThenShowGui("admin_register", AdminCreateHouseFrame::new);
    }
}
