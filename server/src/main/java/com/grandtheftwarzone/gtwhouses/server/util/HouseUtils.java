package com.grandtheftwarzone.gtwhouses.server.util;

import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.data.HouseBlock;
import org.bukkit.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class HouseUtils {

    private static final ArrayList<UUID> rentWarnings = new ArrayList<>();

    public static void resetHouseBlocks(House house, Server server) {
        World world = server.getWorld(house.getWorld());

        //TODO: This can be optimized by storing blocks in map based on their position
        for (int x = house.getMinPosX(); x < house.getMaxPosX(); x++) {
            for (int y = house.getMinPosY(); y < house.getMaxPosY(); y++) {
                for (int z = house.getMinPosZ(); z < house.getMaxPosZ(); z++) {
                    if (world.getBlockAt(x, y, z).getType() == Material.AIR) continue;
                    boolean isHouseBlock = false;

                    for (HouseBlock block : GTWHouses.getManager().getHouseBlocks(house.getName())) {
                        if (block.getX() == x && block.getY() == y && block.getZ() == z) {
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
    public static long getRentTimeToPay(House house) {
        if (!house.isRented()) return 0;

        return new Date().getTime() - house.getRentDueDate().getTime();
    }

    static final long oneDay = 24 * 60 * 60 * 1000;

    public static void handleRent(House house, OfflinePlayer player) {
        // First check for house kicking
        if (house.isKicked() && house.getRenter() != null) {
            GTWHouses.getInstance().getLogger().info("House " + house.getName() + " is kicked");
            int daysSince = (int) ((new Date().getTime() - house.getRentDueDate().getTime()) / (24 * 60 * 60 * 1000));

            if (daysSince >= 3 * oneDay) {
                house.setRenter(null);
                house.setKicked(false);
                house.setRentDueDate(null);

                resetHouseBlocks(house, GTWHouses.getInstance().getServer());

                house.resetRent();
                GTWHouses.getManager().save();

                GTWHouses.getLoginMessageSystem().sendOrStore(player, ChatColor.GREEN + "You have been kicked from house " + ChatColor.YELLOW + house.getName() + ChatColor.GREEN + " because the house is no longer being for rent.");
                return;
            }
        }

        if (!house.isRented()) return;

        long rentTime = getRentTimeToPay(house);
        if (rentTime < oneDay) return;

        double cost = house.getRentCost() * (int) (rentTime / oneDay);

        if (house.isRentedByPlayer()) {
            OfflinePlayer renter = GTWHouses.getInstance().getServer().getOfflinePlayer(house.getRenter());

            // 3 days have passed since the rent was due
            if (rentTime > 3 * oneDay) {
                rentWarnings.remove(renter.getUniqueId());
                house.resetRent();
                GTWHouses.getManager().save();

                resetHouseBlocks(house, GTWHouses.getInstance().getServer());
                GTWHouses.getLoginMessageSystem().sendOrStore(renter, ChatColor.RED + "You have been kicked from house " + ChatColor.YELLOW + house.getName() + ChatColor.RED + " for not paying rent.");
                return;
            }

            //Pay rent normally
            if (GTWHouses.getEconomy().has(renter, cost)) {
                rentWarnings.remove(renter.getUniqueId());
                GTWHouses.getEconomy().withdrawPlayer(renter, cost);
                GTWHouses.getLoginMessageSystem().sendOrStore(renter, "You have paid " + cost + " for " + rentTime + " days of rent");
            } else {
                //Warn the player that he has 3 days to pay
                if (!rentWarnings.contains(renter.getUniqueId())) {
                    GTWHouses.getLoginMessageSystem().sendOrStore(renter, "You don't have enough money to pay for " + rentTime + " days of rent. You have 3 days to pay it all.");
                    rentWarnings.add(renter.getUniqueId());
                } else return;
            }
        }

        house.renewRent();
        GTWHouses.getManager().save();

        GTWHouses.getEconomy().depositPlayer(player, cost);
        GTWHouses.getLoginMessageSystem().sendOrStore(player, "You have received " + cost + " for " + rentTime + " days of rent for house " + house.getName() + "!");
    }

    public static boolean isLocationInHouse(Location location, House house) {
        if (!location.getWorld().getName().equals(house.getWorld())) return false;

        return location.getX() >= house.getMinPosX() && location.getX() <= house.getMaxPosX() &&
                location.getY() >= house.getMinPosY() && location.getY() <= house.getMaxPosY() &&
                location.getZ() >= house.getMinPosZ() && location.getZ() <= house.getMaxPosZ();
    }

}