package com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.AdminShopGUI;
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
public class OpenAdminShopGuiPacket implements IGTWPacket {

    private AdminShopGUI gui;
    private HashMap<String, Shop> shops;
    private HashMap<String, ShopItem> shopItems;

    @Override
    public void fromBytes(ByteBuf buf) {
        gui = AdminShopGUI.values()[buf.readInt()];

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
            shopItems = new HashMap<>(size);
            for (int i = 0; i < size; i++) {
                ShopItem s = new ShopItem();
                s.fromBytes(buf);
                shopItems.put(s.getItem(), s);
            }
        }

    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(gui.ordinal());

        buf.writeBoolean(shops != null);
        if (shops != null) {
            buf.writeInt(shops.size());
            for (Shop s : shops.values())
                s.toBytes(buf);
        }

        buf.writeBoolean(shopItems != null);
        if (shopItems != null) {
            buf.writeInt(shopItems.size());
            for (ShopItem s : shopItems.values())
                s.toBytes(buf);
        }

    }
}
