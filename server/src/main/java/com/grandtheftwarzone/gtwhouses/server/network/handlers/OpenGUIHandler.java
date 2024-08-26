package com.grandtheftwarzone.gtwhouses.server.network.handlers;

import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.OpenGUIC2SPacket;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.HousesGUIS2CPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.network.GTWPacketHandler;
import org.bukkit.entity.Player;

public class OpenGUIHandler implements GTWPacketHandler.PacketHandler<OpenGUIC2SPacket> {
    @Override
    public void handle(Player sender, OpenGUIC2SPacket packet) {
        switch (packet.getType()) {
            case HOUSES:
                GTWHouses.getPacketManager().sendPacket(sender, new HousesGUIS2CPacket(GTWHouses.getManager().getHouses()));
                break;
        }
    }
}
