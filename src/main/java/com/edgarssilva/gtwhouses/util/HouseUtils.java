package com.edgarssilva.gtwhouses.util;

import com.edgarssilva.gtwhouses.GTWHouses;
import com.edgarssilva.gtwhouses.manager.HouseManager;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HouseUtils {

    public static boolean resetHouse(RegionManager rm, House house, World world) throws StorageException {
        if (rm == null) return false;
        if (house == null) return false;

        ProtectedRegion region = rm.getRegion(house.getName());
        if (region == null) return false;

        region.getMembers().removeAll();
        region.getOwners().removeAll();

        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();

        //TODO: This can be optimized
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    if (world.getBlockAt(x, y, z).getType() == Material.AIR) continue;
                    boolean isHouseBlock = false;

                    for (HouseBlock block : house.getBlocks()) {
                        if (block.x == x && block.y == y && block.z == z) {
                            isHouseBlock = true;
                            break;
                        }
                    }

                    if (!isHouseBlock) world.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }

        house.resetOwner();
        rm.save();
        HouseManager.setDirty();
        return true;
    }

    public static void handleRent(Player player, Server server) {
        if (player == null) return;

        List<House> toBeReset = new ArrayList<>();

        for (House h : HouseManager.getPlayerHouses(player.getUniqueId())) {
            long diff = new Date().getTime() - h.getLastRent().getTime();



            int days = (int) diff / (24 * 60 * 60 * 1000);
            int remainingDays = h.getRentDaysDuration() - days;

            String houseName = ChatColor.GOLD + h.getName() + ChatColor.RED;

            switch (remainingDays) {
                case 1:
                    if (h.getRentStatus() != RentStatus.CLOSE_TO_EXPIRE) {
                        player.sendMessage("§cYour house " + houseName + " rent will expire in 1 day.");
                        h.setRentStatus(RentStatus.CLOSE_TO_EXPIRE);
                        HouseManager.setDirty();
                    }
                    break;
                case 0:
                    if (h.getRentStatus() != RentStatus.EXPIRING) {
                        player.sendMessage("§cYour house " + houseName + " rent will expire today.");
                        h.setRentStatus(RentStatus.EXPIRING);
                        HouseManager.setDirty();
                    }
                    break;
                case -1:
                    if (h.getRentStatus() != RentStatus.EXPIRED) {
                        player.sendMessage("§cYour house " + houseName + " rent has expired. You have until today to pay it.");
                        h.setRentStatus(RentStatus.EXPIRED);
                        HouseManager.setDirty();
                    }
                    break;
                default:
                    if (remainingDays < -1) {
                        player.sendMessage("§cYour house " + houseName + " rent has expired. Resetting house...");
                        toBeReset.add(h);
                    }
            }
        }


        for (House h : toBeReset) {
            World world = server.getWorld(h.getWorld());
            RegionManager rm = GTWHouses.getInstance().getWorldGuardPlugin().getRegionManager(world);

            try {
                resetHouse(rm, h, world);
            } catch (StorageException e) {
                GTWHouses.getInstance().getLogger().warning(e.getLocalizedMessage());
            }
        }
    }

    public enum RentStatus {
        EXPIRED, // Expired (still has 1 day to pay)
        CLOSE_TO_EXPIRE, // 1 day left
        EXPIRING, // Today
        RENTED, // More than 1 day left
        NOT_RENTED // Not rented
    }
}
