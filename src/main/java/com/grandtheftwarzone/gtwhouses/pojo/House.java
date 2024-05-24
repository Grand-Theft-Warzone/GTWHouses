package com.grandtheftwarzone.gtwhouses.pojo;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@SerializableAs("House")
public class House implements ConfigurationSerializable {
//    @Setter private int id;

    private final String name;
    private final String world;

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

    public House(String name, String world, Vector minPos, Vector maxPos, double rentCost, double buyCost) {
        this.name = name;
        this.world = world;
        this.minPos = minPos;
        this.maxPos = maxPos;
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

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("name", name);
        map.put("world", world);
        map.put("minPos", minPos);
        map.put("maxPos", maxPos);
        map.put("owner", owner == null ? null : owner.toString());
        map.put("rentCost", rentCost);
        map.put("buyCost", buyCost);
        map.put("renter", renter == null ? null : renter.toString());
        map.put("rentedAt", rentedAt == null ? null : GTWHouses.dateFormat.format(rentedAt));
        map.put("rentDueDate", rentDueDate == null ? null : GTWHouses.dateFormat.format(rentDueDate));
        map.put("kicked", kicked);
        map.put("sellCost", sellCost);
        return map;
    }

    public static House deserialize(Map<String, Object> map) throws Exception {
        String name = (String) map.get("name");
        String world = (String) map.get("world");
        Vector minPos = (Vector) map.get("minPos");
        Vector maxPos = (Vector) map.get("maxPos");
        double rentCost = (double) map.get("rentCost");
        double buyCost = (double) map.get("buyCost");
        House house = new House(name, world, minPos, maxPos, rentCost, buyCost);
        if (map.get("owner") != null) house.setOwner(UUID.fromString((String) map.get("owner")));
        if (map.get("renter") != null) house.setRenter(UUID.fromString((String) map.get("renter")));
        if (map.get("rentedAt") != null) house.setRentedAt(GTWHouses.dateFormat.parse((String) map.get("rentedAt")));
        if (map.get("rentDueDate") != null)
            house.setRentDueDate(GTWHouses.dateFormat.parse((String) map.get("rentDueDate")));
        if (map.get("kicked") != null) house.setKicked((boolean) map.get("kicked"));
        if (map.get("sellCost") != null) house.setSellCost((double) map.get("sellCost"));
        return house;
    }

}
