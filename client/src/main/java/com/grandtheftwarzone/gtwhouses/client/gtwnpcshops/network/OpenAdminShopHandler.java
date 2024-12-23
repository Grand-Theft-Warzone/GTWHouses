package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.network;

import com.grandtheftwarzone.gtwhouses.client.gtwhouses.network.GTWHouseHandler;
import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui.GuiItemPricing;
import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui.GuiShopCreation;
import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui.GuiShopList;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.OpenAdminShopGuiPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class OpenAdminShopHandler implements GTWHouseHandler<OpenAdminShopGuiPacket> {
    @Override
    public void handle(OpenAdminShopGuiPacket message) {
        GuiScreen gui = null;
        switch (message.getGui()) {
            case SHOP_LIST:
                gui = new GuiShopList(message.getShops().values());
                break;
            case ITEMS_PRICING:
                gui = new GuiItemPricing(message.getShopItems());
                break;
            case SHOP_CREATION:
                gui = (new GuiShopCreation(message.getShops(), message.getShopItems()));
                break;
            case SHOP_EDIT:
                //TODO: Implement shop edit GUI
                break;
        }

        if (gui == null) return;

        Minecraft mc = Minecraft.getMinecraft();
        final GuiScreen finalGui = gui;
        mc.addScheduledTask(() -> mc.displayGuiScreen(finalGui));
    }
}
