package com.grandtheftwarzone.gtwhouses.server.gtwnpcshops.handlers;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.OpenAdminShopGuiPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.network.GTWPacketHandler;
import org.bukkit.entity.Player;

public class OpenAdminShopHandler implements GTWPacketHandler.PacketHandler<OpenAdminShopGuiPacket> {
    @Override
    public void handle(Player sender, OpenAdminShopGuiPacket packet) {
        if (sender.hasPermission("gtwhouses.admin")) {
            GTWHouses.getPacketManager().sendPacket(sender, new OpenAdminShopGuiPacket(packet.getGui(), GTWHouses.getShopsManager().getShops(), GTWHouses.getShopsManager().getItems()));
        }
    }
}
