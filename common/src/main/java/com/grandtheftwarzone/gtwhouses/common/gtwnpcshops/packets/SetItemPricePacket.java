package com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItem;
import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SetItemPricePacket implements IGTWPacket {

    private ShopItem item;

    @Override
    public void fromBytes(ByteBuf buf) {
        item = new ShopItem();
        item.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        item.toBytes(buf);
    }
}
