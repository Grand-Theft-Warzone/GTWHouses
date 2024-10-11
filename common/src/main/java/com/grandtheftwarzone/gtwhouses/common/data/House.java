package com.grandtheftwarzone.gtwhouses.common.data;

import com.grandtheftwarzone.gtwhouses.common.GTWHousesUtils;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class House implements Serializable {
    private String name;
    private String world;

    @Setter
    private UUID owner;

    private int minPosX;
    private int minPosY;
    private int minPosZ;

    private int maxPosX;
    private int maxPosY;
    private int maxPosZ;

    private int rentCost;
    private int buyCost;

    @Setter
    private UUID renter;
    @Setter
    private Date rentedAt;
    @Setter
    private Date rentDueDate;
    @Setter
    private boolean kicked;

    private double sellCost;

    private HouseType type = HouseType.HIGH_END;

    private String imageURL;

    private static final int HOUSE_VERSION = 1;

    public House(String name, String world, int rentCost, int buyCost, HouseType type) {
        this.name = name;
        this.world = world;
        this.rentCost = rentCost;
        this.buyCost = buyCost;
        this.rentedAt = null;
        this.rentDueDate = null;
        this.owner = null;
        this.renter = null;
        this.sellCost = -1;
        this.type = type;
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
        buf.writeInt(HOUSE_VERSION);

        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(nameBytes.length);
        buf.writeBytes(nameBytes);

        byte[] worldBytes = world.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(worldBytes.length);
        buf.writeBytes(worldBytes);

        buf.writeInt(minPosX);
        buf.writeInt(minPosY);
        buf.writeInt(minPosZ);
        buf.writeInt(maxPosX);
        buf.writeInt(maxPosY);
        buf.writeInt(maxPosZ);
        buf.writeInt(rentCost);
        buf.writeInt(buyCost);

        byte[] ownerBytes = owner == null ? new byte[0] : owner.toString().getBytes(StandardCharsets.UTF_8);
        buf.writeInt(ownerBytes.length);
        buf.writeBytes(ownerBytes);
        byte[] renterBytes = renter == null ? new byte[0] : renter.toString().getBytes(StandardCharsets.UTF_8);
        buf.writeInt(renterBytes.length);
        buf.writeBytes(renterBytes);

        byte[] rentedAtBytes = rentedAt == null ? new byte[0] : GTWHousesUtils.DATE_FORMAT.format(rentedAt).getBytes(StandardCharsets.UTF_8);
        buf.writeInt(rentedAtBytes.length);
        buf.writeBytes(rentedAtBytes);

        byte[] rentDueDateBytes = rentDueDate == null ? new byte[0] : GTWHousesUtils.DATE_FORMAT.format(rentDueDate).getBytes(StandardCharsets.UTF_8);
        buf.writeInt(rentDueDateBytes.length);
        buf.writeBytes(rentDueDateBytes);

        buf.writeBoolean(kicked);
        buf.writeDouble(sellCost);

        buf.writeInt(type.ordinal());

        byte[] imageURLBytes = imageURL.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(imageURLBytes.length);
        buf.writeBytes(imageURLBytes);
    }

    public static House fromBytes(ByteBuf buf) {
        int version = buf.readInt();

        if (version == 1) {
            String name = readString(buf);
            String world = readString(buf);

            int minPosX = buf.readInt();
            int minPosY = buf.readInt();
            int minPosZ = buf.readInt();
            int maxPosX = buf.readInt();
            int maxPosY = buf.readInt();
            int maxPosZ = buf.readInt();
            int rentCost = buf.readInt();
            int buyCost = buf.readInt();


            String ownerString = readString(buf);
            UUID owner = ownerString.isEmpty() ? null : UUID.fromString(ownerString);

            String renterString = readString(buf);
            UUID renter = renterString.isEmpty() ? null : UUID.fromString(renterString);

            String rentedAtString = readString(buf);
            String rentDueDateString = readString(buf);


            Date rentedAt = null;
            Date rentDueDate = null;

            try {
                rentedAt = rentedAtString.isEmpty() ? null : GTWHousesUtils.DATE_FORMAT.parse(rentedAtString);
                rentDueDate = rentDueDateString.isEmpty() ? null : GTWHousesUtils.DATE_FORMAT.parse(rentDueDateString);
            } catch (Exception ignored) {
            }

            boolean kicked = buf.readBoolean();
            double sellCost = buf.readDouble();

            HouseType type = HouseType.values()[buf.readInt()];

            String imageURL = readString(buf);

            return new House(name, world, owner, minPosX, minPosY, minPosZ, maxPosX, maxPosY, maxPosZ, rentCost, buyCost, renter, rentedAt, rentDueDate, kicked, sellCost, type, imageURL);
        }

        return null;
    }

    private static String readString(ByteBuf buf) {
        byte[] bytes = new byte[buf.readInt()];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
