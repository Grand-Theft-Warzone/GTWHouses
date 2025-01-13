package com.grandtheftwarzone.gtwhouses.server.gtwnpcshops.handlers;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.UpdateShopPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.network.GTWPacketHandler;
import org.bukkit.entity.Player;

public class UpdateShopHandler implements GTWPacketHandler.PacketHandler<UpdateShopPacket> {
    @Override
    public void handle(Player sender, UpdateShopPacket packet) {
        if (!sender.hasPermission("gtwhouses.admin")) {
            sender.sendMessage("You do not have permission to update shops.");
            return;
        }
        GTWHouses.getShopsManager().removeShop(packet.getOriginalName());
        GTWHouses.getShopsManager().setOrUpdateShop(packet.getShop());
    }
}
