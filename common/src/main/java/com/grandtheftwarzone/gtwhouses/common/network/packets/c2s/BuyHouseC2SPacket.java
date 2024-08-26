package com.grandtheftwarzone.gtwhouses.common.network.packets.c2s;

import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BuyHouseC2SPacket implements IGTWPacket {
    private String houseName;

    @Override
    public void fromBytes(ByteBuf buf) {
        byte[] bytes = new byte[buf.readByte()];
        buf.readBytes(bytes);
        houseName = new String(bytes);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] bytes = houseName.getBytes();
        buf.writeByte(bytes.length);
        buf.writeBytes(bytes);
    }
}
