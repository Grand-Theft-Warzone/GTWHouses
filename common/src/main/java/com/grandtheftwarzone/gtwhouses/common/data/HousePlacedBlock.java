package com.grandtheftwarzone.gtwhouses.common.data;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class HousePlacedBlock implements Serializable {
    private int x;
    private int y;
    private int z;

    public static HousePlacedBlock fromBytes(ByteBuf buf) {
        return new HousePlacedBlock(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    public int getHash() {
        return Objects.hash(x, y, z);
    }

    public static int getHash(int x, int y, int z) {
        return Objects.hash(x, y, z);
    }
}
