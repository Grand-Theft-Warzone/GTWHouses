package com.grandtheftwarzone.gtwhouses.dao;

import com.grandtheftwarzone.gtwhouses.database.HouseDatabase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.util.Vector;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
public class House implements Serializable {
    @Setter private int id;

    private final String name;
    private final UUID world;
    private final List<HouseBlock> blocks;

    private final Vector minPos;
    private final Vector maxPos;

    @Setter private UUID owner;

    private final double rentCost;
    private final double buyCost;

    //Rent
    @Setter private UUID renter;
    @Setter private Date rentedAt;
    @Setter private Date rentDueDate;
    @Setter private boolean kicked;

    //Selling
    @Setter private double sellCost;

    public House(ResultSet rs, List<HouseBlock> blocks) throws Exception {
        this.id = rs.getInt("id");
        this.name = rs.getString("name");
        this.world = UUID.fromString(rs.getString("world_uuid"));
        this.minPos = new Vector(rs.getInt("minX"), rs.getInt("minY"), rs.getInt("minZ"));
        this.maxPos = new Vector(rs.getInt("maxX"), rs.getInt("maxY"), rs.getInt("maxZ"));
        String ownerUUIDString = rs.getString("owner_uuid");
        this.owner = ownerUUIDString == null ? null : UUID.fromString(ownerUUIDString);
        String renterUUIDString = rs.getString("renter_uuid");
        this.renter = renterUUIDString == null ? null : UUID.fromString(renterUUIDString);
        this.blocks = blocks;
        this.rentCost = rs.getDouble("rent_cost");
        this.buyCost = rs.getDouble("buy_cost");
        String rentedAtString = rs.getString("rented_at");
        this.rentedAt = rentedAtString == null ? null : HouseDatabase.sqliteDateFormat.parse(rentedAtString);
        String rentDueDateString = rs.getString("rent_due");
        this.rentDueDate = rentDueDateString == null ? null : HouseDatabase.sqliteDateFormat.parse(rentDueDateString);
        this.sellCost = rs.getDouble("sell_cost");
        this.kicked = rs.getBoolean("kicked");
    }

    public House(String name, UUID world, Vector minPos, Vector maxPos, List<HouseBlock> blocks, double rentCost, double buyCost) {
        this.name = name;
        this.world = world;
        this.minPos = minPos;
        this.maxPos = maxPos;
        this.blocks = blocks;
        this.rentCost = rentCost;
        this.buyCost = buyCost;
        this.rentedAt = null;
        this.rentDueDate = null;
        this.owner = null;
        this.renter = null;
        this.sellCost = -1;
    }

    public void setUnowned() {
        owner = null;
        sellCost = -1;
        rentedAt = null;
        rentDueDate = null;
        renter = null;
        kicked = false;
    }

    public boolean isOwned() {
        return owner != null;
    }

    public boolean isRented() {
        return rentedAt != null || rentDueDate != null;
    }

    public boolean isRentable() {
        return isRented() && renter == null; // If it's rented to the city but not to a player
    }

    public boolean isRentedByPlayer() {
        return isRented() && (renter != null || kicked);
    }

    public boolean isForSale() {
        return !isOwned() || sellCost != -1;
    }

    public void resetRent() {
        renter = null;
        rentedAt = null;
        rentDueDate = null;
        sellCost = -1;
        kicked = false;
    }

    public void startRent() {
        sellCost = -1;
        rentedAt = new Date();
        rentDueDate = new Date(rentedAt.getTime() + 24L * 60L * 60L * 1000L);  // 1 day
        kicked = false;
    }

    public void renewRent() {
        rentDueDate = new Date(rentDueDate.getTime() + 24L * 60L * 60L * 1000L); //1 day
    }
}
