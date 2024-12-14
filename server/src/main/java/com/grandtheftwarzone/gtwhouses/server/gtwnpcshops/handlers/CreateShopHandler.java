package com.grandtheftwarzone.gtwhouses.server.gtwnpcshops.handlers;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.CreateShopPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.network.GTWPacketHandler;
import org.bukkit.entity.Player;

public class CreateShopHandler implements GTWPacketHandler.PacketHandler<CreateShopPacket> {
    @Override
    public void handle(Player sender, CreateShopPacket packet) {
        if (!sender.hasPermission("gtwhouses.admin")) {
            sender.sendMessage("You do not have permission to set item prices.");
            return;
        }
        GTWHouses.getShopsManager().setOrUpdateShop(packet.getShop());
    }
}
