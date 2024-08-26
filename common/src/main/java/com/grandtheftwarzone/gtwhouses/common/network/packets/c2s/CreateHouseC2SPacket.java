package com.grandtheftwarzone.gtwhouses.common.network.packets.c2s;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.data.HouseBlock;
import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateHouseC2SPacket implements IGTWPacket {
    private House house;
    private ArrayList<HouseBlock> blocks;

    @Override
    public void fromBytes(ByteBuf buf) {
        house = House.fromBytes(buf);
        int size = buf.readInt();
        blocks = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            blocks.add(new HouseBlock(buf.readInt(), buf.readInt(), buf.readInt()));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        house.toBytes(buf);
        buf.writeInt(blocks.size());
        for (HouseBlock block : blocks) {
            buf.writeInt(block.getX());
            buf.writeInt(block.getY());
            buf.writeInt(block.getZ());
        }
    }
}