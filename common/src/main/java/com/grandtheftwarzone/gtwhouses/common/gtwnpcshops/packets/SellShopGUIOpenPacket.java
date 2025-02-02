package com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItem;
import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SellShopGUIOpenPacket implements IGTWPacket {
    private List<ShopItem> shopItems;

    @Override
    public void fromBytes(ByteBuf buf) {
        shopItems = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            ShopItem item = new ShopItem();
            item.fromBytes(buf);
            shopItems.add(item);
        }

    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(shopItems.size());
        for (ShopItem item : shopItems)
            item.toBytes(buf);

    }
}
