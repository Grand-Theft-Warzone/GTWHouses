package com.edgarssilva.gtwhouses.events;

import com.edgarssilva.gtwhouses.GTWHouses;
import com.edgarssilva.gtwhouses.util.House;
import com.edgarssilva.gtwhouses.manager.HouseManager;
import com.edgarssilva.gtwhouses.util.HouseBlock;
import com.edgarssilva.gtwhouses.world_guard.GTWHousesFlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Set;

public class HouseBlockBreak implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location location = block.getLocation();

        RegionManager regionManager = GTWHouses.getInstance().getWorldGuardPlugin().getRegionManager(player.getWorld());

        Set<ProtectedRegion> regions = regionManager.getApplicableRegions(location).getRegions();

        for (ProtectedRegion region : regions) {
            House house = HouseManager.getHouse(region.getId());
            if (house == null) continue;

            World world = player.getServer().getWorld(house.getWorld());

            for (HouseBlock houseBlock : house.getBlocks()) {
                if (!houseBlock.getLocation(world).equals(event.getBlock().getLocation()))
                    continue;

                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey! " + ChatColor.RESET + ChatColor.GRAY + "You can't break a house block.");
                event.setCancelled(true);
                return;
            }
        }
    }
}
