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


    public static int getRemainingRentDays(House house) {
        long diff = new Date().getTime() - house.getLastRent().getTime();
        int days = (int) diff / (24 * 60 * 60 * 1000);
        return house.getRentDaysDuration() - days;
    }

    public static void handleRent(Player player, boolean requested) {
        if (player == null)  return;

        Server server = player.getServer();
        if (server == null) return;

        List<House> toBeReset = new ArrayList<>();

        for (House h : HouseManager.getPlayerHouses(player.getUniqueId())) {
            if (h.getRentStatus() == RentStatus.NOT_RENTED) continue;

            RentStatus status = RentStatus.fromDays(getRemainingRentDays(h));

            if (requested || status != h.getRentStatus())
                player.sendMessage(status.getWarning(h));

            if (status != h.getRentStatus()) {
                h.setRentStatus(status);
                HouseManager.setDirty();
            }

            if (status == RentStatus.OVERDUE) toBeReset.add(h);
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
        OVERDUE, // More than 1 day overdue
        EXPIRED, // Expired (still has 1 day to pay)
        CLOSE_TO_EXPIRE, // 1 day left
        EXPIRING, // Today
        RENTED, // More than 1 day left
        NOT_RENTED // Not rented
        ;

        public static RentStatus fromDays(int daysLeft) {
            switch (daysLeft) {
                case 0:
                    return EXPIRING;
                case 1:
                    return CLOSE_TO_EXPIRE;
                case -1:
                    return EXPIRED;
                default:
                    return daysLeft < -1 ? OVERDUE : RENTED;
            }
        }

        public String getWarning(House house) {
            String houseName = ChatColor.GOLD + house.getName() + ChatColor.RED;

            int daysLeft = getRemainingRentDays(house);

            switch (this) {
                case EXPIRED:
                    return "§cYour house " + houseName + " rent has expired. You have until today to pay it.";
                case CLOSE_TO_EXPIRE:
                    return "§cYour house " + houseName + " rent will expire in 1 day.";
                case EXPIRING:
                    return "§cYour house " + houseName + " rent will expire today.";
                case OVERDUE:
                    return "§cYour house " + houseName + " rent is overdue. Resetting house...";
                default:
                    return "You house " + houseName + ChatColor.RESET + " rent will expire in " + ChatColor.YELLOW + daysLeft + ChatColor.RESET + " day" + (daysLeft > 1 ? "s" : "") + ".";
            }
        }
    }
}
