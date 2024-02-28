package com.grandtheftwarzone.gtwhouses.util;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.dao.House;
import com.grandtheftwarzone.gtwhouses.dao.HouseBlock;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Date;

public class HouseUtils {

    public static void resetHouseBlocks(House house, Server server) {
        World world = server.getWorld(house.getWorld());

        Vector min = house.getMinPos();
        Vector max = house.getMaxPos();

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
    }

    //TODO: Test this function
    public static int getRentDaysToPay(House house) {
        if (!house.isRented()) return 0;

        long diff = new Date().getTime() - house.getRentDueDate().getTime();
        return (int) diff / (24 * 60 * 60 * 1000);
    }

    public static void handleRent(House house, Player player) {
        if (!house.isRented()) return;

        int payDays = getRentDaysToPay(house);
        if (payDays <= 0) return;

        if (!GTWHouses.getHouseDatabase().payRent(house)) return;

        double cost = house.getRentCost() * payDays;
        GTWHouses.getEconomy().depositPlayer(player, cost);

        player.sendMessage(ChatColor.GREEN + "You have received " + cost + " for " + payDays + " days of rent");
    }

    public static boolean isLocationInHouse(Location location, House house) {
        Vector min = house.getMinPos();
        Vector max = house.getMaxPos();

        return location.getX() >= min.getX() && location.getX() <= max.getX() &&
                location.getY() >= min.getY() && location.getY() <= max.getY() &&
                location.getZ() >= min.getZ() && location.getZ() <= max.getZ();

    }

}