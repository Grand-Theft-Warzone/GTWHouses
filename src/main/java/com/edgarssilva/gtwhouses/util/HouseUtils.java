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

        Server server = GTWHouses.getInstance().getServer();

        if (house.isOwned()) house.setOwner(null); //TODO: Also send message to owner
        if (house.isRentable()) {
            if (house.getRent().isRented()) {
                house.getRent().setRenewable(false);
                Player renter = server.getPlayer(house.getRent().getRenter());
                if (renter != null)
                    renter.sendMessage("Your house" + house.getName() + "has changed owner. You can no longer renew your rent.");
                //TODO: Send message to renter when he logs in
            }
        }

        rm.save();
        HouseManager.setDirty();
        return true;
    }


    public static int getRemainingRentDays(HouseRent rent) {
        if (rent == null || !rent.isRented()) return -1;

        long diff = new Date().getTime() - rent.getStartDate().getTime();
        int days = (int) diff / (24 * 60 * 60 * 1000);
        return rent.getDaysDuration() - days;
    }

    public static void handleRent(Player player, boolean requested) {
        if (player == null) return;

        Server server = player.getServer();
        if (server == null) return;

        List<House> toBeReset = new ArrayList<>();

        for (House h : HouseManager.getPlayerRenterHouses(player.getUniqueId())) {
            HouseRent rent = h.getRent();
            if (!rent.isRented()) continue;

            HouseRent.RentStatus status = HouseRent.RentStatus.fromDays(getRemainingRentDays(rent));

            if (requested || status != rent.getStatus())
                player.sendMessage(status.getWarning(h));

            if (status != rent.getStatus()) {
                rent.setStatus(status);
                HouseManager.setDirty();
            }

            if (status == HouseRent.RentStatus.OVERDUE) toBeReset.add(h);
        }

        for (House h : toBeReset) {
            World world = server.getWorld(h.getWorldUUID());
            RegionManager rm = GTWHouses.getInstance().getWorldGuardPlugin().getRegionManager(world);

            try {
                resetHouse(rm, h, world);
            } catch (StorageException e) {
                GTWHouses.getInstance().getLogger().warning(e.getLocalizedMessage());
            }
        }
    }

}
