package com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.Shop;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItem;
import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OpenAdminShopPacket implements IGTWPacket {


    private HashMap<String, Shop> shops;
    private HashMap<String, ShopItem> items;

    @Override
    public void fromBytes(ByteBuf buf) {
        shops = new HashMap<>();
        items = new HashMap<>();

        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            Shop shop = new Shop();
            shop.fromBytes(buf);
            shops.put(shop.getName(), shop);
        }

        size = buf.readInt();
        for (int i = 0; i < size; i++) {
            ShopItem item = new ShopItem();
            item.fromBytes(buf);
            items.put(item.getItem(), item);
        }

    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(shops.size());
        for (Shop shop : shops.values()) {
            shop.toBytes(buf);
        }
        buf.writeInt(items.size());
        for (ShopItem item : items.values()) {
            item.toBytes(buf);
        }
    }
}
