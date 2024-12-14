package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.network;

import com.grandtheftwarzone.gtwhouses.client.gtwhouses.network.GTWHouseHandler;
import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui.GuiItemPricing;
import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui.GuiShopCreation;
import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui.GuiShopList;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.OpenAdminShopGuiPacket;
import net.minecraft.client.Minecraft;

public class OpenAdminShopHandler implements GTWHouseHandler<OpenAdminShopGuiPacket> {
    @Override
    public void handle(OpenAdminShopGuiPacket message) {
        switch (message.getGui()) {
            case SHOP_LIST:
                Minecraft.getMinecraft().displayGuiScreen(new GuiShopList(message.getShops().values()));
                break;
            case ITEMS_PRICING:
                Minecraft.getMinecraft().displayGuiScreen(new GuiItemPricing(message.getShopItems()));
                break;
            case SHOP_CREATION:
                Minecraft.getMinecraft().displayGuiScreen(new GuiShopCreation(message.getShops(), message.getShopItems()));
                break;
            case SHOP_EDIT:
                //TODO: Implement shop edit GUI
                break;
        }
    }
}
