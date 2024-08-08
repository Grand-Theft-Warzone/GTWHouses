package com.grandtheftwarzone.gtwhouses.client.network;

import com.grandtheftwarzone.gtwhouses.client.GTWHousesUI;
import com.grandtheftwarzone.gtwhouses.client.network.handlers.HouseUIHandler;
import com.grandtheftwarzone.gtwhouses.common.GTWHousesPacket;
import com.grandtheftwarzone.gtwhouses.common.GTWHousesUtils;
import com.grandtheftwarzone.gtwhouses.common.packets.HouseUIPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class NetworkHandler {
    private static FMLEventChannel CHANNEL;

    public static void init() {
        CHANNEL = NetworkRegistry.INSTANCE.newEventDrivenChannel(GTWHousesUtils.CHANNEL_NAME);
        CHANNEL.register(NetworkHandler.class);
    }

    public static void sendToServer(IMessage message) {
        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
        new GTWHousesPacket(message).toBytes(buf);

        //  Minecraft.getMinecraft().player.connection.sendPacket(new CPacketCustomPayload("gtwhouses", buf));
        CHANNEL.sendToServer(new FMLProxyPacket(buf, GTWHousesUtils.CHANNEL_NAME));
    }

    @SubscribeEvent
    public static void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        if (!event.getPacket().channel().equals(GTWHousesUtils.CHANNEL_NAME)) return;
        PacketBuffer buf = new PacketBuffer(event.getPacket().payload());
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);

        ByteBuf byteBuf = Unpooled.copiedBuffer(data);
        GTWHousesPacket packet = new GTWHousesPacket();
        packet.fromBytes(byteBuf);

        GTWHousesUI.getLogger().info("Packet received! payload: " + packet.getType().clazz.getName());

        switch (packet.getType()) {
            case HOUSE_UI:
                new HouseUIHandler().handle((HouseUIPacket) packet.getPacket());
                break;
            default:
                GTWHousesUI.getLogger().warn("Received unknown packet type: " + packet.getType());
        }
    }
}
