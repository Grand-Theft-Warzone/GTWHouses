package com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.Shop;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItem;
import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShopGUIOpenPacket implements IGTWPacket {

    private Shop shop;
    private Collection<ShopItem> items;

    @Override
    public void fromBytes(ByteBuf buf) {
        shop = new Shop();
        shop.fromBytes(buf);

        items = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            ShopItem item = new ShopItem();
            item.fromBytes(buf);
            items.add(item);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        shop.toBytes(buf);

        buf.writeInt(items.size());
        for (ShopItem item : items) {
            item.toBytes(buf);
        }
    }
}
