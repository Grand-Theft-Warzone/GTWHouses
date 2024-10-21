package com.grandtheftwarzone.gtwhouses.server.events;

import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.OpenGUIC2SPacket;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.HousesGUIS2CPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockInteractEvent implements Listener {

    public void openGUIOnBlockInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        if (block.getType().name().equalsIgnoreCase("dynamxmod:gtwprops.block_laptop") ||
                block.getType().name().equalsIgnoreCase("4102:1"))
            GTWHouses.getPacketManager().sendPacket(event.getPlayer(), new HousesGUIS2CPacket(OpenGUIC2SPacket.OpenGUIType.HOUSES, GTWHouses.getManager().getHouses()));
    }
}
