package com.grandtheftwarzone.gtwhouses.server.network;

import com.grandtheftwarzone.gtwhouses.common.GTWUtil;
import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class GTWPacketHandler {
    private final HashMap<Class<? extends IGTWPacket>, ArrayList<PacketHandler<?>>> handlers = new HashMap<>();

    public <T extends IGTWPacket> void handle(Player sender, T packet) {
        ArrayList<PacketHandler<?>> handlers = this.handlers.get(packet.getClass());
        if (handlers == null) return;

        for (PacketHandler<?> handler : handlers)
            ((PacketHandler<T>) handler).handle(sender, packet);
    }

    public void registerHandler(PacketHandler<? extends IGTWPacket> handler) {
        Class<?> clazz = GTWUtil.getGenericType(handler.getClass());
        if (clazz != null) {
            ArrayList<PacketHandler<?>> handlers = this.handlers.get(clazz);
            if (handlers == null) {
                handlers = new ArrayList<>();
                this.handlers.put((Class<? extends IGTWPacket>) clazz, handlers);
            }
            handlers.add(handler);
        }
    }

    public interface PacketHandler<T extends IGTWPacket> {
        void handle(Player sender, T packet);

    }

}
