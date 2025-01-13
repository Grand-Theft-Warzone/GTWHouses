package com.grandtheftwarzone.gtwhouses.server.gtwnpcshops.handlers;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.OpenAdminShopGuiPacket;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.OpenEditShopPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.network.GTWPacketHandler;
import org.bukkit.entity.Player;

public class OpenEditShopHandler implements GTWPacketHandler.PacketHandler<OpenEditShopPacket> {
    @Override
    public void handle(Player sender, OpenEditShopPacket packet) {
        if (sender.hasPermission("gtwhouses.admin")) {
            GTWHouses.getPacketManager().sendPacket(sender, new OpenEditShopPacket(packet.getShopName(), GTWHouses.getShopsManager().getShops(), GTWHouses.getShopsManager().getItems()));
        }
    }
}
