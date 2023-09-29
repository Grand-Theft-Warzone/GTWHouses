package com.edgarssilva.gtwhouses.util;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class House implements Serializable {

    private String name;
    private List<HouseBlock> blocks;
    private double rent;

    private UUID owner;


    public House(String name, List<HouseBlock> blocks, double rent) {
        new House(name, blocks, rent, null);
    }

    public House(String name, List<HouseBlock> blocks, double rent, UUID owner) {
        this.name = name;
        this.blocks = blocks;
        this.rent = rent;
        this.owner = owner;
    }


    public String getName() {
        return name;
    }

    public List<HouseBlock> getBlocks() {
        return blocks;
    }

    public double getRent() {
        return rent;
    }

    public void setRent(double rent) {
        this.rent = rent;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }
}
