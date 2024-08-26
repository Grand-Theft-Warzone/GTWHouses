package com.grandtheftwarzone.gtwhouses.server.network.handlers;

import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.CreateHouseC2SPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.network.GTWPacketHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CreateHouseHandler implements GTWPacketHandler.PacketHandler<CreateHouseC2SPacket> {
    @Override
    public void handle(Player sender, CreateHouseC2SPacket packet) {
        if (!sender.isOp() || !sender.hasPermission("house.houseadmin")) {
            sender.sendMessage("You do not have permission to use this command.");
        }

        GTWHouses.getManager().addHouse(packet.getHouse(), packet.getBlocks());
        GTWHouses.getManager().save();

        sender.sendMessage(ChatColor.GREEN + "House registered successfully.");
    }
}
