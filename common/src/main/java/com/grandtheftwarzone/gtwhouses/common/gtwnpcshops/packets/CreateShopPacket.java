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
public class CreateShopPacket implements IGTWPacket {

    private Shop shop;

    @Override
    public void fromBytes(ByteBuf buf) {
        shop = new Shop();
        shop.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        shop.toBytes(buf);
    }
}
