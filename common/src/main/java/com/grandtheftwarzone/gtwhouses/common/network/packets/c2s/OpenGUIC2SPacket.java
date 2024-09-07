package com.grandtheftwarzone.gtwhouses.common.network.packets.c2s;

import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OpenGUIC2SPacket implements IGTWPacket {
    private OpenGUIType type;

    @Override
    public void fromBytes(ByteBuf buf) {
        type = OpenGUIType.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.ordinal());
    }

    public enum OpenGUIType {
        HOUSES,
    }
}
