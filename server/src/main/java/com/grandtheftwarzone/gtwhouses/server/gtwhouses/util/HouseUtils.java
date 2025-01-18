package com.grandtheftwarzone.gtwhouses.server.gtwhouses.util;

import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.data.HousePlacedBlock;
import org.bukkit.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class HouseUtils {

    private static final ArrayList<UUID> rentWarnings = new ArrayList<>();

    public static void resetHouseBlocks(House house, Server server) {
        World world = server.getWorld(house.getWorld());
        for (HousePlacedBlock block : GTWHouses.getHousesManager().getPlacedBlocks(house.getName()).values())
            world.getBlockAt(block.getX(), block.getY(), block.getZ()).setType(Material.AIR);
    }

    //TODO: Test this function
    public static long getRentTimeToPay(House house) {
        if (!house.isRented()) return 0;

        return new Date().getTime() - house.getRentDueDate().getTime();
    }

    static long oneDay = 24 * 60 * 60 * 1000;

    public static void handleRent(House house, OfflinePlayer player) {
        // First check for house kicking
        if (house.isKicked() && house.getRenter() != null) {
            GTWHouses.getInstance().getLogger().info("House " + house.getName() + " is kicked");

            long timeUntil = house.getRentDueDate().getTime() - new Date().getTime();

            if (timeUntil <= 0) {
                house.setRenter(null);
                house.setKicked(false);
                house.setRentDueDate(null);

                resetHouseBlocks(house, GTWHouses.getInstance().getServer());

                house.resetRent();
                GTWHouses.getHousesManager().save();

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
                GTWHouses.getHousesManager().save();

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
        GTWHouses.getHousesManager().save();

        GTWHouses.getEconomy().depositPlayer(player, cost);
        GTWHouses.getLoginMessageSystem().sendOrStore(player, "You have received " + cost + " for " + rentTime + " days of rent for house " + house.getName() + "!");
    }

    public static boolean isLocationInHouse(Location location, House house) {
        if (!location.getWorld().getName().equals(house.getWorld())) return false;

        return location.getX() >= house.getMinPosX() && location.getX() <= house.getMaxPosX() &&
                location.getY() >= house.getMinPosY() && location.getY() <= house.getMaxPosY() &&
                location.getZ() >= house.getMinPosZ() && location.getZ() <= house.getMaxPosZ();
    }


    /*public static List<HousePlacedBlock> getHouseBlocks(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, World world) {
        List<HousePlacedBlock> blocks = new ArrayList<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() != Material.AIR)
                        blocks.add(new HousePlacedBlock(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static List<HousePlacedBlock> getHouseBlocks(House house) {
        return getHouseBlocks(house.getMinPosX(), house.getMinPosY(), house.getMinPosZ(), house.getMaxPosX(), house.getMaxPosY(), house.getMaxPosZ(), Bukkit.getWorld(house.getWorld()));
    }*/

}