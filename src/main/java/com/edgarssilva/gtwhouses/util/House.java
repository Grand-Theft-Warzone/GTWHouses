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

    private double rentPerDay;

    private Date lastRent;

    private int rentDaysDuration;

    private HouseUtils.RentStatus rentStatus;

    public House(String name, UUID world, List<HouseBlock> blocks, double rentPerDay) {
        this.name = name;
        this.world = world;
        this.blocks = blocks;
        this.rentPerDay = rentPerDay;
        this.rentStatus = HouseUtils.RentStatus.NOT_RENTED;
    }

    public String getName() {
        return name;
    }

    public List<HouseBlock> getBlocks() {
        return blocks;
    }

    public double getRentPerDay() {
        return rentPerDay;
    }

    public void setRentPerDay(double rentPerDay) {
        this.rentPerDay = rentPerDay;
    }

    public void setOwner(UUID owner, int rentDaysDuration) {
        this.owner = owner;
        this.rentDaysDuration = rentDaysDuration;
        this.setLastRent(new Date());
        this.rentStatus = HouseUtils.RentStatus.RENTED;
    }

    public UUID getOwner() {
        return owner;
    }

    public int getRentDaysDuration() {
        return rentDaysDuration;
    }

    public void setLastRent(Date lastRent) {
        this.lastRent = lastRent;
    }

    public Date getLastRent() {
        return lastRent;
    }

    public void setRentStatus(HouseUtils.RentStatus rentStatus) {
        this.rentStatus = rentStatus;
    }

    public HouseUtils.RentStatus getRentStatus() {
        return rentStatus;
    }

    public UUID getWorld() {
        return world;
    }

    public void resetOwner() {
        this.owner = null;
        this.rentDaysDuration = 0;
        this.lastRent = null;
        this.rentStatus = HouseUtils.RentStatus.NOT_RENTED;
    }
}
