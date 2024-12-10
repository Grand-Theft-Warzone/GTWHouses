package com.grandtheftwarzone.gtwhouses.server.gtwhouses.events;

import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.OpenGUIC2SPacket;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.HousesGUIS2CPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockInteractEvent implements Listener {

    @EventHandler
    public void openGUIOnBlockInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        //if (block.getType().name().equalsIgnoreCase("DYNAMXMOD_GTWPROPSBLOCK_LAPTOP") &&
                //block.getData() == 1)
        if(block.getType() == Material.DIRT)
            GTWHouses.getPacketManager().sendPacket(event.getPlayer(), new HousesGUIS2CPacket(OpenGUIC2SPacket.OpenGUIType.HOUSES, GTWHouses.getHousesManager().getHouses()));
    }
}
