package com.grandtheftwarzone.gtwhouses.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@AllArgsConstructor
public class HouseBlock implements Serializable {
    private int id;
    public final int x;
    public final int y;
    public final int z;

    public HouseBlock(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.x = rs.getInt("x");
        this.y = rs.getInt("y");
        this.z = rs.getInt("z");
    }

    public Location getLocation(World world) {
        return new Location(world, x, y, z);
    }
}