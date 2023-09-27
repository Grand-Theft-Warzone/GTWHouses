package com.edgarssilva.gtwhouses.events;

import com.edgarssilva.gtwhouses.GTWHouses;
import com.edgarssilva.gtwhouses.world_guard.GTWHousesFlagRegistry;
import com.sk89q.worldedit.regions.Regions;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Set;

public class HouseBlockBreak implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        RegionManager regionManager = GTWHouses.getInstance().getWorldGuardPlugin().getRegionManager(player.getWorld());

        Set<ProtectedRegion> regions =  regionManager.getApplicableRegions(player.getLocation()).getRegions();
        for (ProtectedRegion region : regions) {
            if (region.getFlag(GTWHousesFlagRegistry.HOUSE) != null && Boolean.TRUE.equals(region.getFlag(GTWHousesFlagRegistry.HOUSE))) {
//                if (!region.isOwner(player.getUniqueId())) {
//                    event.setCancelled(true);
//                    player.sendMessage("You can't break blocks here.");
//                }
            }
        }
    }
}
