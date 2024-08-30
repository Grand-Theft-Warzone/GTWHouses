package com.grandtheftwarzone.gtwhouses.server.handlers;

import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.HouseActionC2SPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouseAction;
import com.grandtheftwarzone.gtwhouses.server.network.GTWPacketHandler;
import org.bukkit.entity.Player;

public class HouseActionHandler implements GTWPacketHandler.PacketHandler<HouseActionC2SPacket>{
    @Override
    public void handle(Player sender, HouseActionC2SPacket packet) {
        try {
           GTWHouseAction.handle(packet.getAction(), sender, packet.getHouseName(), packet.getArgs());
        } catch (GTWHouseAction.InvalidActionException e) {
            sender.sendMessage(e.getMessage());
        }
    }
}
