package com.grandtheftwarzone.gtwhouses.client.network;

import com.grandtheftwarzone.gtwhouses.client.GTWHousesUI;
import com.grandtheftwarzone.gtwhouses.client.network.handlers.HouseCoordsHandler;
import com.grandtheftwarzone.gtwhouses.client.network.handlers.HousesGUIHandler;
import com.grandtheftwarzone.gtwhouses.client.network.handlers.RegisterHouseHandler;
import com.grandtheftwarzone.gtwhouses.common.GTWHousesUtils;
import com.grandtheftwarzone.gtwhouses.common.GTWUtil;
import com.grandtheftwarzone.gtwhouses.common.network.GTWHousesPacket;
import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import java.util.HashMap;

public class GTWNetworkHandler {
    private static FMLEventChannel CHANNEL;

    //TODO: Change one handler to a list of handlers
    private static final HashMap<Class<? extends IGTWPacket>, GTWHouseHandler<?>> handlers = new HashMap<>();

    public static void init() {
        CHANNEL = NetworkRegistry.INSTANCE.newEventDrivenChannel(GTWHousesUtils.CHANNEL_NAME);
        CHANNEL.register(GTWNetworkHandler.class);


        registerHandler(new HousesGUIHandler());
        registerHandler(new HouseCoordsHandler());
        registerHandler(new RegisterHouseHandler());
    }

    //NOTE: Generics are used, but the class could be sent as a parameter instead
    public static <T extends IGTWPacket> void registerHandler(GTWHouseHandler<T> handler) {
        Class<?> clazz = GTWUtil.getGenericType(handler.getClass());
        if (clazz == null) return;

        handlers.put((Class<? extends IGTWPacket>) clazz, handler);
    }


    public static void sendToServer(IGTWPacket message) {
        GTWHousesUI.getLogger().info("Sending packet to server: " + message.getClass().getName());
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

        if (packet.isUnknown()) {
            GTWHousesUI.getLogger().warn("Received unknown packet type: " + packet.getClazz());
            return;
        }

        if (!handlers.containsKey(packet.getClazz())) {
            GTWHousesUI.getLogger().warn("No handler found for packet type: " + packet.getClazz().getName());
            return;
        }

        handle(packet.getClazz(), packet.getPacket());
    }

    private static <T extends IGTWPacket> void handle(Class<? extends IGTWPacket> clazz, T packet) {
        ((GTWHouseHandler<T>) handlers.get(clazz)).handle(packet);
    }
}
