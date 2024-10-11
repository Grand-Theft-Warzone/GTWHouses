package com.grandtheftwarzone.gtwhouses.server.handlers;

import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.CreateHouseC2SPacket;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.HouseImagesS2CPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.network.GTWPacketHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;

public class CreateHouseHandler implements GTWPacketHandler.PacketHandler<CreateHouseC2SPacket> {
    @Override
    public void handle(Player sender, CreateHouseC2SPacket packet) {
        if (!sender.isOp() || !sender.hasPermission("house.houseadmin")) {
            sender.sendMessage("You do not have permission to use this command.");
        }

        //If it is editing a house, remove the old house
        GTWHouses.getManager().removeHouse(packet.getOriginalHouseName());

        GTWHouses.getManager().addHouse(packet.getHouse(), HouseCoordsHandler.houseBlocks.get(sender.getUniqueId()));
        GTWHouses.getManager().save();

        HouseCoordsHandler.houseBlocks.remove(sender.getUniqueId());

        sender.sendMessage(ChatColor.GREEN + "House registered successfully.");


        //TODO: Make this in a separate method and maybe thread
        for (Player p : GTWHouses.getInstance().getServer().getOnlinePlayers())
            GTWHouses.getPacketManager().sendPacket(p, new HouseImagesS2CPacket(Collections.singletonList(packet.getHouse().getImageURL())));
    }
}
