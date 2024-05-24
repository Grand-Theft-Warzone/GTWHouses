package com.grandtheftwarzone.gtwhouses.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@SerializableAs("HouseBlock")
public class HouseBlock implements ConfigurationSerializable {
    public final int x;
    public final int y;
    public final int z;

   /* public HouseBlock(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.x = rs.getInt("x");
        this.y = rs.getInt("y");
        this.z = rs.getInt("z");
    }*/

    public Location getLocation(World world) {
        return new Location(world, x, y, z);
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        return map;
    }

    public static HouseBlock deserialize(Map<String, Object> map) {
        return new HouseBlock( (int) map.get("x"), (int) map.get("y"), (int) map.get("z"));
    }

}