package com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItemAmount;
import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BuyItemsPacket implements IGTWPacket {

    private List<ShopItemAmount> items;

    @Override
    public void fromBytes(ByteBuf buf) {
        items = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            byte[] keyBytes = new byte[buf.readInt()];
            buf.readBytes(keyBytes);
            String key = new String(keyBytes);
            int amount = buf.readInt();
            items.add(new ShopItemAmount(key, amount));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(items.size());
        items.forEach(item -> {
            byte[] keyBytes = item.getItemStackSerialized().getBytes();
            buf.writeInt(keyBytes.length);
            buf.writeBytes(keyBytes);
            buf.writeInt(item.getAmount());
        });

    }
}
