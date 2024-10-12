package com.grandtheftwarzone.gtwhouses.common.network.packets.s2c;

import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HouseCoordsS2CPacket implements IGTWPacket {
    private String name;
    private int buyCost;
    private int rentCost;
    private int type;

    private int minX;
    private int minY;
    private int minZ;
    private int maxX;
    private int maxY;
    private int maxZ;

    private String worldName;
    private String imageURL;

    private String originalName;


    @Override
    public void fromBytes(ByteBuf buf) {
        buyCost = buf.readInt();
        rentCost = buf.readInt();
        type = buf.readInt();

        byte[] nameBytes = new byte[buf.readInt()];
        buf.readBytes(nameBytes);
        name = new String(nameBytes);

        minX = buf.readInt();
        minY = buf.readInt();
        minZ = buf.readInt();
        maxX = buf.readInt();
        maxY = buf.readInt();
        maxZ = buf.readInt();


        byte[] worldBytes = new byte[buf.readInt()];
        buf.readBytes(worldBytes);
        worldName = new String(worldBytes);

        byte[] imageBytes = new byte[buf.readInt()];
        buf.readBytes(imageBytes);
        imageURL = new String(imageBytes);


        byte[] originalNameBytes = new byte[buf.readInt()];
        buf.readBytes(originalNameBytes);
        originalName = new String(originalNameBytes);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(buyCost);
        buf.writeInt(rentCost);
        buf.writeInt(type);

        byte[] nameBytes = name.getBytes();
        buf.writeInt(nameBytes.length);
        buf.writeBytes(nameBytes);

        buf.writeInt(minX);
        buf.writeInt(minY);
        buf.writeInt(minZ);
        buf.writeInt(maxX);
        buf.writeInt(maxY);
        buf.writeInt(maxZ);


        byte[] worldBytes = worldName.getBytes();
        buf.writeInt(worldBytes.length);
        buf.writeBytes(worldBytes);

        byte[] imageBytes = imageURL.getBytes();
        buf.writeInt(imageBytes.length);
        buf.writeBytes(imageBytes);


        byte[] originalNameBytes = originalName.getBytes();
        buf.writeInt(originalNameBytes.length);
        buf.writeBytes(originalNameBytes);
    }

}
