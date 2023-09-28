package com.edgarssilva.gtwhouses.util;

import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;
import java.util.UUID;

public class HouseBlock implements Serializable {
    public final UUID world;
    public final double x;
    public final double y;
    public final double z;

    public HouseBlock(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.world = location.getWorld().getUID();
    }

    public HouseBlock(World world, int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world.getUID();
    }
}