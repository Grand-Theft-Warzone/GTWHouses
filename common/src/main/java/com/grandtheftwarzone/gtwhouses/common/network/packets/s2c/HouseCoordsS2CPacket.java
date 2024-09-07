package com.grandtheftwarzone.gtwhouses.common.network.packets.s2c;

import com.grandtheftwarzone.gtwhouses.common.data.HouseBlock;
import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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

    private List<HouseBlock> blocks;


    @Override
    public void fromBytes(ByteBuf buf) {
        buyCost = buf.readInt();
        rentCost = buf.readInt();
        type = buf.readInt();

        name = buf.readCharSequence(buf.readInt(), Charset.defaultCharset()).toString();

        minX = buf.readInt();
        minY = buf.readInt();
        minZ = buf.readInt();
        maxX = buf.readInt();
        maxY = buf.readInt();
        maxZ = buf.readInt();

        blocks = new ArrayList<>();
        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            blocks.add(new HouseBlock(buf.readInt(), buf.readInt(), buf.readInt()));
        }

        byte[] worldBytes = new byte[buf.readInt()];
        buf.readBytes(worldBytes);
        worldName = new String(worldBytes);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(buyCost);
        buf.writeInt(rentCost);
        buf.writeInt(type);
        buf.writeInt(name.length());
        buf.writeCharSequence(name, Charset.defaultCharset());

        buf.writeInt(minX);
        buf.writeInt(minY);
        buf.writeInt(minZ);
        buf.writeInt(maxX);
        buf.writeInt(maxY);
        buf.writeInt(maxZ);


        buf.writeInt(blocks.size());

        for (HouseBlock block : blocks) {
            buf.writeInt(block.getX());
            buf.writeInt(block.getY());
            buf.writeInt(block.getZ());
        }


        byte[] worldBytes =  worldName.getBytes();
        buf.writeInt(worldBytes.length);
        buf.writeBytes(worldBytes);
    }
}
