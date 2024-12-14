package com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets;

import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteShopPacket implements IGTWPacket {
    private String shopName;
    @Override
    public void fromBytes(ByteBuf buf) {
        byte[] bytes = new byte[buf.readInt()];
        buf.readBytes(bytes);
        shopName = new String(bytes);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] bytes = shopName.getBytes();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }
}
