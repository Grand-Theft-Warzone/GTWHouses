package com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets;

import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SellItemsPacket implements IGTWPacket {

    private Map<String, Integer> items;

    @Override
    public void fromBytes(ByteBuf buf) {
        items = new HashMap<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            byte[] keyBytes = new byte[buf.readInt()];
            buf.readBytes(keyBytes);
            String key = new String(keyBytes);
            int value = buf.readInt();
            items.put(key, value);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(items.size());
        items.forEach((key, value) -> {
            byte[] keyBytes = key.getBytes();
            buf.writeInt(keyBytes.length);
            buf.writeBytes(keyBytes);

            buf.writeInt(value);
        });

    }
}
