package com.grandtheftwarzone.gtwhouses.common.data;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class HouseBlock implements Serializable {
    private int x;
    private int y;
    private int z;

    public static HouseBlock fromBytes(ByteBuf buf) {
        return new HouseBlock(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }
}
