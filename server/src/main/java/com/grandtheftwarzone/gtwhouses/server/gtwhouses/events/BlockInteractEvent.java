package com.grandtheftwarzone.gtwhouses.server.gtwhouses.events;

import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.OpenGUIC2SPacket;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.HousesGUIS2CPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BlockInteractEvent implements Listener {

    private static final Set<UUID> cooldown = new HashSet<>();

    @EventHandler
    public void openGUIOnBlockInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (cooldown.contains(event.getPlayer().getUniqueId())) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        if (!(block.getType().name().equalsIgnoreCase("DYNAMXMOD_GTWPROPSBLOCK_LAPTOP") &&
                block.getData() == 1))
            return;

        cooldown.add(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskTimer(GTWHouses.getInstance(), () -> cooldown.remove(event.getPlayer().getUniqueId()), 20 * 2, 1); // 2 seconds
        GTWHouses.getPacketManager().sendPacket(event.getPlayer(), new HousesGUIS2CPacket(OpenGUIC2SPacket.OpenGUIType.HOUSES, GTWHouses.getHousesManager().getHouses()));


    }
}
