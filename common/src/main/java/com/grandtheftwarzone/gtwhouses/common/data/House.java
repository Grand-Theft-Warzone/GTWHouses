package com.grandtheftwarzone.gtwhouses.common.data;

import com.grandtheftwarzone.gtwhouses.common.GTWHousesUtils;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class House {
    private String name;
    private String world;

    @Setter private UUID owner;

    private int minPosX;
    private int minPosY;
    private int minPosZ;

    private int maxPosX;
    private int maxPosY;
    private int maxPosZ;

    private double rentCost;
    private double buyCost;

    @Setter private UUID renter;
    @Setter private Date rentedAt;
    @Setter private Date rentDueDate;
    @Setter private boolean kicked;

    private double sellCost;

     public House(String name, String world, double rentCost, double buyCost) {
        this.name = name;
        this.world = world;
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
        return rentDueDate != null;
    }

    public boolean isRentable() {
        return rentedAt != null && renter == null; // If it's rented to the city but not to a player
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

    public void toBytes(ByteBuf buf) {
        buf.writeCharSequence(name, StandardCharsets.UTF_8);
        buf.writeCharSequence(world, StandardCharsets.UTF_8);
        buf.writeInt(minPosX);
        buf.writeInt(minPosY);
        buf.writeInt(minPosZ);
        buf.writeInt(maxPosX);
        buf.writeInt(maxPosY);
        buf.writeInt(maxPosZ);
        buf.writeDouble(rentCost);
        buf.writeDouble(buyCost);
        buf.writeCharSequence(owner == null ? "" : owner.toString(), StandardCharsets.UTF_8);
        buf.writeCharSequence(renter == null ? "" : renter.toString(), StandardCharsets.UTF_8);
        buf.writeCharSequence(rentedAt == null ? "" : GTWHousesUtils.DATE_FORMAT.format(rentedAt), StandardCharsets.UTF_8);
        buf.writeCharSequence(rentDueDate == null ? "" : GTWHousesUtils.DATE_FORMAT.format(rentDueDate), StandardCharsets.UTF_8);
        buf.writeBoolean(kicked);
        buf.writeDouble(sellCost);
    }

    public static House fromBytes(ByteBuf buf) {
        String name = buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString();
        String world = buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString();
        int minPosX = buf.readInt();
        int minPosY = buf.readInt();
        int minPosZ = buf.readInt();
        int maxPosX = buf.readInt();
        int maxPosY = buf.readInt();
        int maxPosZ = buf.readInt();
        double rentCost = buf.readDouble();
        double buyCost = buf.readDouble();
        UUID owner = buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString().isEmpty() ? null : UUID.fromString(buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString());
        UUID renter = buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString().isEmpty() ? null : UUID.fromString(buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString());

        String rentedAtString = buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString();
        String rentDueDateString = buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString();

        Date rentedAt = null;
        Date rentDueDate = null;

        try {
            rentedAt = rentedAtString.isEmpty() ? null : GTWHousesUtils.DATE_FORMAT.parse(rentedAtString);
            rentDueDate = rentDueDateString.isEmpty() ? null : GTWHousesUtils.DATE_FORMAT.parse(rentDueDateString);
        } catch (Exception ignored) {
        }

        boolean kicked = buf.readBoolean();
        double sellCost = buf.readDouble();
        return new House(name, world, owner, minPosX, minPosY, minPosZ, maxPosX, maxPosY, maxPosZ, rentCost, buyCost, renter, rentedAt, rentDueDate, kicked, sellCost);
    }
}
