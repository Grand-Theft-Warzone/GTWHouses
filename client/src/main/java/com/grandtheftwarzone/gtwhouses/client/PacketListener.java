package com.grandtheftwarzone.gtwhouses.client;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class PacketListener {

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void onPacketReceived(FMLNetworkEvent.ClientCustomPacketEvent event) {
        System.out.println("Client Packet received!");
        System.out.println(event.getPacket().payload().toString());
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void onPacketReceivedServer(FMLNetworkEvent.ServerCustomPacketEvent event) {
       // GTWHousesUI.getLogger().info("Server Packet received!");
       // System.out.println("Server Packet received!");
        //System.out.println(event.getPacket().payload().toString());
    }
}
