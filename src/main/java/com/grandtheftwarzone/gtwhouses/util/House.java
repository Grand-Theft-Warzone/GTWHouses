package com.grandtheftwarzone.gtwhouses.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.util.Vector;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Getter
public class House implements Serializable {
    private int id;
    private final String name;
    private final List<HouseBlock> blocks;
    private final UUID world;
    private UUID owner;
    private final double defaultCost;
    @Setter private HouseRent rent;
    private double sellCost;
    private final Vector minPos;
    private final Vector maxPos;
    private final double defaultRentConst;

    public House(ResultSet rs, HouseRent rent, List<HouseBlock> blocks) throws SQLException {
        this.id = rs.getInt("id");
        this.name = rs.getString("name");
        this.world = UUID.fromString(rs.getString("world_uuid"));
        this.minPos = new Vector(rs.getInt("minX"), rs.getInt("minY"), rs.getInt("minZ"));
        this.maxPos = new Vector(rs.getInt("maxX"), rs.getInt("maxY"), rs.getInt("maxZ"));
        String ownerUUIDString = rs.getString("owner_uuid");
        this.owner = ownerUUIDString == null ? null : UUID.fromString(ownerUUIDString);
        this.defaultRentConst = rs.getDouble("base_rent_cost");
        this.defaultCost = rs.getDouble("base_buy_cost");
        this.sellCost = rs.getDouble("sell_cost");
        this.rent = rent;
        this.blocks = blocks;
    }

    public House(String name, UUID world, Vector minPos, Vector maxPos, List<HouseBlock> blocks, double costPerDay, double buyCost) {
        this.name = name;
        this.world = world;
        this.minPos = minPos;
        this.maxPos = maxPos;
        this.blocks = blocks;
        this.defaultRentConst = costPerDay;
        this.defaultCost = buyCost;
        this.setOwner(null); // Leave this before the rent (it makes the house unrentable)
        this.rent = new HouseRent(costPerDay);
    }

    public boolean isSellable() {
        return !isOwned() || sellCost > 0;
    }

    public void setSellable(double sellCost) {
        this.sellCost = sellCost;
    }

    public double getCost() {
        if (isOwned()) return sellCost;
        else return defaultCost;
    }

    public boolean isRentable() {
        return (rent != null && rent.isRenewable());
    }

    public void setRentable(double costPerDay) {
        this.rent = new HouseRent(costPerDay);
    }

    public void resetRent() {
        setRentable(defaultRentConst);
    }

    public void setUnrentable() {
        if (isRentable() && getRent().isRented())
            this.rent.setRenewable(false);
        else this.rent = null;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        this.sellCost = -1;
        setUnrentable();
    }

    public boolean isOwned() {
        return owner != null;
    }
}
