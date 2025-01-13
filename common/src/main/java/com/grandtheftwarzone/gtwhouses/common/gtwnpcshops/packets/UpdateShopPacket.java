package com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.Shop;
import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateShopPacket implements IGTWPacket {

    private String originalName;
    private Shop shop;

    @Override
    public void fromBytes(ByteBuf buf) {
        byte[] nameBytes = new byte[buf.readInt()];
        buf.readBytes(nameBytes);
        originalName = new String(nameBytes);

        shop = new Shop();
        shop.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] nameBytes = originalName.getBytes();
        buf.writeInt(nameBytes.length);
        buf.writeBytes(nameBytes);

        shop.toBytes(buf);
    }
}

