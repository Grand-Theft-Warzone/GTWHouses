package com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data;

import com.grandtheftwarzone.gtwhouses.common.GTWSerializable;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Shop extends GTWSerializable {
    private String name;
    private List<String> items;

    private final int SHOP_VERSION = 1;

    @Override
    public void fromBytes(ByteBuf buf) {
        int version = buf.readInt();

        if (version == 1) {
            int nameLength = buf.readInt();
            byte[] nameBytes = new byte[nameLength];
            buf.readBytes(nameBytes);
            name = new String(nameBytes);

            items = new ArrayList<>();
            int itemsSize = buf.readInt();
            for (int i = 0; i < itemsSize; i++) {
                int itemNameLength = buf.readInt();
                byte[] itemNameBytes = new byte[itemNameLength];
                buf.readBytes(itemNameBytes);
                String itemName = new String(itemNameBytes);

                items.add(itemName);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(SHOP_VERSION);

        byte[] nameBytes = name.getBytes();
        buf.writeInt(nameBytes.length);
        buf.writeBytes(nameBytes);

        buf.writeInt(items.size());
        for (String item : items) {
            byte[] itemNameBytes = item.getBytes();
            buf.writeInt(itemNameBytes.length);
            buf.writeBytes(itemNameBytes);
        }

    }
}
