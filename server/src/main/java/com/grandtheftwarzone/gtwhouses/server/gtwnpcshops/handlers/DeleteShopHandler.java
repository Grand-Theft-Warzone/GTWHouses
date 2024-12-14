package com.grandtheftwarzone.gtwhouses.server.gtwnpcshops.handlers;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.DeleteShopPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.network.GTWPacketHandler;
import org.bukkit.entity.Player;

public class DeleteShopHandler implements GTWPacketHandler.PacketHandler<DeleteShopPacket> {
    @Override
    public void handle(Player sender, DeleteShopPacket packet) {
        if (!sender.hasPermission("gtwhouses.admin")) {
            sender.sendMessage("You do not have permission to set item prices.");
            return;
        }

        GTWHouses.getShopsManager().removeShop(packet.getShopName());

    }
}
