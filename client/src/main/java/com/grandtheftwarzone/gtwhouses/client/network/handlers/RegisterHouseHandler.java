package com.grandtheftwarzone.gtwhouses.client.network.handlers;


import com.grandtheftwarzone.gtwhouses.client.network.GTWHouseHandler;
import com.grandtheftwarzone.gtwhouses.client.ui.frames.AdminCreateHouseFrame;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.RegisterHouseS2CPacket;
import fr.aym.acsguis.api.ACsGuiApi;

public class RegisterHouseHandler implements GTWHouseHandler<RegisterHouseS2CPacket>{
    @Override
    public void handle(RegisterHouseS2CPacket message) {
        ACsGuiApi.asyncLoadThenShowGui("admin_register", AdminCreateHouseFrame::new);
    }
}
