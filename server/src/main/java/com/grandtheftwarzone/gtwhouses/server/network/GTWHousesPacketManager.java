package com.grandtheftwarzone.gtwhouses.server.network;

import com.grandtheftwarzone.gtwhouses.common.GTWHousesUtils;
import com.grandtheftwarzone.gtwhouses.common.network.GTWHousesPacket;
import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import com.grandtheftwarzone.gtwhouses.server.network.TinyProtocol.TinyProtocol;
import com.grandtheftwarzone.gtwhouses.server.network.handlers.BuyHouseHandler;
import com.grandtheftwarzone.gtwhouses.server.network.handlers.CreateHouseHandler;
import com.grandtheftwarzone.gtwhouses.server.network.handlers.HouseCoordsHandler;
import com.grandtheftwarzone.gtwhouses.server.network.handlers.OpenGUIHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import net.minecraft.server.v1_12_R1.PacketDataSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayInCustomPayload;
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomPayload;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class GTWHousesPacketManager extends TinyProtocol {
    public GTWPacketHandler packetHandler;

    public GTWHousesPacketManager(Plugin plugin) {
        super(plugin);
        packetHandler = new GTWPacketHandler();

        packetHandler.registerHandler(new HouseCoordsHandler());
        packetHandler.registerHandler(new CreateHouseHandler());
        packetHandler.registerHandler(new OpenGUIHandler());

        packetHandler.registerHandler(new BuyHouseHandler());
    }

    @Override
    public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
        if (packet instanceof PacketPlayInCustomPayload) {
            PacketPlayInCustomPayload payload = (PacketPlayInCustomPayload) packet;
            if (payload.a().equals(GTWHousesUtils.CHANNEL_NAME)) {

                GTWHousesPacket gtwhousesPacket = new GTWHousesPacket();
                gtwhousesPacket.fromBytes(payload.b());

                if (gtwhousesPacket.isUnknown()) {
                    plugin.getLogger().warning("Received unknown packet type: " + gtwhousesPacket.getClazz());
                } else {
                    plugin.getLogger().info("Received packet: " + gtwhousesPacket.getClazz().getName());
                    packetHandler.handle(sender, gtwhousesPacket.getPacket());
                }
                return null; // Cancel the packet
            }
        }

        return super.onPacketInAsync(sender, channel, packet);
    }

    public void sendPacket(Player player, IGTWPacket packet) {
        PacketDataSerializer serializer = new PacketDataSerializer(Unpooled.buffer());
        GTWHousesPacket payload = new GTWHousesPacket(packet);
        payload.toBytes(serializer);

        plugin.getLogger().info("Sending packet: " + payload.getClazz().getName());

        PacketPlayOutCustomPayload out = new PacketPlayOutCustomPayload(GTWHousesUtils.CHANNEL_NAME, serializer);
        super.sendPacket(player, out);
    }
}
