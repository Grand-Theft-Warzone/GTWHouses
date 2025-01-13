package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.network;

import com.grandtheftwarzone.gtwhouses.client.gtwhouses.network.GTWHouseHandler;
import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui.GuiShopEdit;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.AdminShopGUI;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.Shop;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.OpenAdminShopGuiPacket;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.OpenEditShopPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class OpenEditShopHandler implements GTWHouseHandler<OpenEditShopPacket> {
    @Override
    public void handle(OpenEditShopPacket message) {
        Shop shop = message.getShops().get(message.getShopName());
        Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() -> mc.displayGuiScreen(new GuiShopEdit(shop, message.getShops(), message.getShopItems())));
    }
}
