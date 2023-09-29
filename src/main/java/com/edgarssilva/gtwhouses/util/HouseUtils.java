package com.edgarssilva.gtwhouses.util;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Material;
import org.bukkit.World;

public class HouseUtils {

    public static boolean resetHouse(RegionManager rm, House house, World world) throws StorageException {
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

        rm.save();
        return true;
    }
}
