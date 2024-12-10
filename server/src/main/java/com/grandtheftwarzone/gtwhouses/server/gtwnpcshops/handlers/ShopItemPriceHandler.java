package com.grandtheftwarzone.gtwhouses.server.gtwnpcshops.handlers;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.SetItemPricePacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.network.GTWPacketHandler;
import org.bukkit.entity.Player;

public class ShopItemPriceHandler implements GTWPacketHandler.PacketHandler<SetItemPricePacket> {

    @Override
    public void handle(Player sender, SetItemPricePacket packet) {
        if (!sender.hasPermission("gtwhouses.admin")) {
            sender.sendMessage("You do not have permission to set item prices.");
            return;
        }

        GTWHouses.getShopsManager().setOrUpdateItem(packet.getItem());
    }
}
