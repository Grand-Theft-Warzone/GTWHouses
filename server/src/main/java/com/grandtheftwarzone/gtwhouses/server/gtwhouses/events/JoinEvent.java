package com.grandtheftwarzone.gtwhouses.server.gtwhouses.events;

import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.HouseImagesS2CPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GTWHouses.getPacketManager().sendPacket(player, new HouseImagesS2CPacket(GTWHouses.getHousesManager().getImagesURLs()));
    }
}
