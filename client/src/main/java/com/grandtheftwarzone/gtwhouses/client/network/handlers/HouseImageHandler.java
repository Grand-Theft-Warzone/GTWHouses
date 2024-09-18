package com.grandtheftwarzone.gtwhouses.client.network.handlers;

import com.grandtheftwarzone.gtwhouses.client.GTWHousesUI;
import com.grandtheftwarzone.gtwhouses.client.houseimages.HouseImagesManager;
import com.grandtheftwarzone.gtwhouses.client.network.GTWHouseHandler;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.HouseImagesS2CPacket;

public class HouseImageHandler implements GTWHouseHandler<HouseImagesS2CPacket> {
    @Override
    public void handle(HouseImagesS2CPacket message) {
        HouseImagesManager.loadImages(message.getHouseURLS());

        GTWHousesUI.getLogger().info("Loaded "+message.getHouseURLS().size()+" house images.");
    }
}
