package com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.Shop;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItem;
import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OpenEditShopPacket implements IGTWPacket {
    private String shopName;
    private HashMap<String, Shop> shops;
    private List<ShopItem> shopItems;

    @Override
    public void fromBytes(ByteBuf buf) {
        byte[] bytes = new byte[buf.readInt()];
        buf.readBytes(bytes);
        shopName = new String(bytes);


        if (buf.readBoolean()) {
            int size = buf.readInt();
            shops = new HashMap<>(size);
            for (int i = 0; i < size; i++) {
                Shop s = new Shop();
                s.fromBytes(buf);
                shops.put(s.getName(), s);
            }
        }

        if (buf.readBoolean()) {
            int size = buf.readInt();
            shopItems = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ShopItem s = new ShopItem();
                s.fromBytes(buf);
                shopItems.add(s);
            }
        }

    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] bytes = shopName.getBytes();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);

        buf.writeBoolean(shops != null);
        if (shops != null) {
            buf.writeInt(shops.size());
            for (Shop s : shops.values())
                s.toBytes(buf);
        }

        buf.writeBoolean(shopItems != null);
        if (shopItems != null) {
            buf.writeInt(shopItems.size());
            for (ShopItem s : shopItems)
                s.toBytes(buf);
        }

    }
}
