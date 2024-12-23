package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.network;

import com.grandtheftwarzone.gtwhouses.client.gtwhouses.network.GTWHouseHandler;
import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui.GuiShop;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.ShopGUIOpenPacket;
import net.minecraft.client.Minecraft;

public class OpenShopHandler implements GTWHouseHandler<ShopGUIOpenPacket> {
    @Override
    public void handle(ShopGUIOpenPacket message) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() -> mc.displayGuiScreen(new GuiShop(message.getShop(), message.getItems())));
    }
}
