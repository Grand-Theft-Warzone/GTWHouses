package com.grandtheftwarzone.gtwhouses.common;

import io.netty.buffer.ByteBuf;

public abstract class GTWSerializable {

    public GTWSerializable() {
    }

    public abstract void fromBytes(ByteBuf buf);

    public abstract void toBytes(ByteBuf buf);

}
