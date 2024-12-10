package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.network;

import com.grandtheftwarzone.gtwhouses.client.gtwhouses.network.GTWHouseHandler;
import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui.GuiShop;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.ShopGUIOpenPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class OpenShopHandler implements GTWHouseHandler<ShopGUIOpenPacket> {
    @Override
    public void handle(ShopGUIOpenPacket message) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiShop(message.getShop(), message.getItems()));
    }
}
