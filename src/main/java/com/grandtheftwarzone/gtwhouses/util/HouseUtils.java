package com.grandtheftwarzone.gtwhouses.util;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.dao.House;
import com.grandtheftwarzone.gtwhouses.dao.HouseBlock;
import com.grandtheftwarzone.gtwhouses.dao.HouseRent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
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

    public static int getRemainingRentDays(HouseRent rent) {
        if (rent == null || !rent.isRented()) return -1;

        long diff = new Date().getTime() - rent.getRentedAt().getTime();
        int days = (int) diff / (24 * 60 * 60 * 1000);
        return rent.getDaysRented() - days;
    }

    public static void handleRent(House house, Player player, boolean requested) {

        HouseRent rent = house.getRent();
        if (!rent.isRented()) return;

        HouseRent.RentStatus status = HouseRent.RentStatus.fromDays(getRemainingRentDays(rent));

        if (requested || (status != HouseRent.RentStatus.NOT_RENTED && status != HouseRent.RentStatus.RENTED)) {
            if (player != null) player.sendMessage(status.getWarning(house));
        }

        if (status.shouldReset(house)) {
            resetHouseBlocks(house, GTWHouses.getInstance().getServer());
            if (house.getRent() != null)
                GTWHouses.getHouseDatabase().stopRent(house);
        }

    }

    public static boolean isLocationInHouse(Location location, House house) {
        Vector min = house.getMinPos();
        Vector max = house.getMaxPos();

        return location.getX() >= min.getX() && location.getX() <= max.getX() &&
                location.getY() >= min.getY() && location.getY() <= max.getY() &&
                location.getZ() >= min.getZ() && location.getZ() <= max.getZ();

    }

}