package com.grandtheftwarzone.gtwhouses.server.network;

import com.grandtheftwarzone.gtwhouses.common.GTWHousesPacket;
import com.grandtheftwarzone.gtwhouses.common.GTWHousesUtils;
import com.grandtheftwarzone.gtwhouses.server.network.TinyProtocol.TinyProtocol;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import net.minecraft.server.v1_12_R1.PacketDataSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayInCustomPayload;
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomPayload;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class GTWHousesPacketManager extends TinyProtocol {

    public GTWHousesPacketManager(Plugin plugin) {
        super(plugin);
    }

    @Override
    public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
        if (packet instanceof PacketPlayInCustomPayload) {
            PacketPlayInCustomPayload payload = (PacketPlayInCustomPayload) packet;
            if (payload.a().equals(GTWHousesUtils.CHANNEL_NAME)) {

                GTWHousesPacket gtwhousesPacket = new GTWHousesPacket();
                gtwhousesPacket.fromBytes(payload.b());

                //TODO: Implement the packet handlers

                plugin.getLogger().info("Received packet: " + gtwhousesPacket.getType());

                return null; // Cancel the packet
            }
        }

        return super.onPacketInAsync(sender, channel, packet);
    }

    public void sendPacket(Player player, IMessage packet) {
        PacketDataSerializer serializer = new PacketDataSerializer(Unpooled.buffer());
        GTWHousesPacket payload = new GTWHousesPacket(packet);
        payload.toBytes(serializer);

        PacketPlayOutCustomPayload out = new PacketPlayOutCustomPayload(GTWHousesUtils.CHANNEL_NAME, serializer);
        super.sendPacket(player, out);
    }
}
