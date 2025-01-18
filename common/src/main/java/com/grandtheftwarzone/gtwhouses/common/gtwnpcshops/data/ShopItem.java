package com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data;

import com.grandtheftwarzone.gtwhouses.common.GTWSerializable;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopItem extends GTWSerializable {
    private String serializedItemStack;
    private int buyPrice;
    private int buyLevel;


    private int sellPrice;

    private final int SHOP_ITEM_VERSION = 1;


    @Override
    public void fromBytes(ByteBuf buf) {
        int version = buf.readInt();
        if (version == 1) {
            int itemNameLength = buf.readInt();
            byte[] itemNameBytes = new byte[itemNameLength];
            buf.readBytes(itemNameBytes);
            serializedItemStack = new String(itemNameBytes);

            buyPrice = buf.readInt();
            buyLevel = buf.readInt();
            sellPrice = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(SHOP_ITEM_VERSION);

        byte[] itemNameBytes = serializedItemStack.getBytes();
        buf.writeInt(itemNameBytes.length);
        buf.writeBytes(itemNameBytes);

        buf.writeInt(buyPrice);
        buf.writeInt(buyLevel);
        buf.writeInt(sellPrice);

    }
}
