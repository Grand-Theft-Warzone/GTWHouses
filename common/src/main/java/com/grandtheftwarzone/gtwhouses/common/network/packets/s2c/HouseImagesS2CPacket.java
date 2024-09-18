package com.grandtheftwarzone.gtwhouses.common.network.packets.s2c;

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
public class HouseImagesS2CPacket implements IGTWPacket {
    List<String> houseURLS = new ArrayList<>();

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            byte[] bytes = new byte[buf.readInt()];
            buf.readBytes(bytes);
            houseURLS.add(new String(bytes));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(houseURLS.size());
        for (String url : houseURLS) {
            byte[] bytes = url.getBytes();
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        }
    }
}
