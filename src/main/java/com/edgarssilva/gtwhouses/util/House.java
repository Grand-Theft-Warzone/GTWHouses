package com.edgarssilva.gtwhouses.util;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class House implements Serializable {

    private final String name;

    private final List<HouseBlock> blocks;

    private final UUID world;

    private UUID owner;

    private double rentConst;
    private double defaultCost;

    private Date lastRent;

    private int rentDaysDuration;

    private HouseRent rent;

    private double sellCost;

    public House(String name, UUID world, List<HouseBlock> blocks, double costPerDay, double buyCost) {
        this.name = name;
        this.world = world;
        this.blocks = blocks;
        this.rentConst = costPerDay;
        this.defaultCost = buyCost;
        this.rent = new HouseRent(costPerDay);
        this.sellCost = -1;
    }

    public boolean isSellable() {
        return sellCost != -1 && !isOwned();
    }

    public void setSellable(double sellCost) {
        this.sellCost = sellCost;
    }

    public double getSellCost() {
        return sellCost;
    }

    public boolean isRentable() {
        return rent != null;
    }

    public void setRentable(HouseRent rent) {
        this.rent = rent;
    }

    public void resetRent() {
        setRentable(new HouseRent(rentConst));
    }

    public void setUnrentable() {
        setRentable(null);
    }

    public HouseRent getRent() {
        return rent;
    }

    public double getDefaultCost() {
        return defaultCost;
    }

    public String getName() {
        return name;
    }

    public List<HouseBlock> getBlocks() {
        return blocks;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        this.sellCost = -1;
    }

    public boolean isOwned() {
        return owner != null;
    }

    public UUID getOwner() {
        return owner;
    }

    public UUID getWorldUUID() {
        return world;
    }
}
