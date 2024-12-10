package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.network;

import com.grandtheftwarzone.gtwhouses.client.gtwhouses.network.GTWHouseHandler;
import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui.GuiItemPricing;
import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui.GuiShopCreation;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.OpenAdminShopPacket;
import net.minecraft.client.Minecraft;

public class OpenAdminShopHandler implements GTWHouseHandler<OpenAdminShopPacket> {
    @Override
    public void handle(OpenAdminShopPacket message) {
        //Minecraft.getMinecraft().displayGuiScreen(new GuiItemPricing(message.getItems()));
        System.out.println("Packet: " + message.getItems().size());
        Minecraft.getMinecraft().displayGuiScreen(new GuiShopCreation(message.getShops(), message.getItems()));
    }
}
