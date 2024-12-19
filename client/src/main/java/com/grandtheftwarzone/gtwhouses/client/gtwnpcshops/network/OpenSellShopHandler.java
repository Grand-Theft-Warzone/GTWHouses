package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.network;

import com.grandtheftwarzone.gtwhouses.client.gtwhouses.network.GTWHouseHandler;
import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui.GuiSell;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.SellShopGUIOpenPacket;
import net.minecraft.client.Minecraft;

public class OpenSellShopHandler implements GTWHouseHandler<SellShopGUIOpenPacket> {
    @Override
    public void handle(SellShopGUIOpenPacket message) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiSell(message.getShopItems()));
    }
}
