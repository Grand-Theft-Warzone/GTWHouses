package com.grandtheftwarzone.gtwhouses.common.network.packets.c2s;

import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.nio.charset.Charset;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HouseCoordsC2SPacket implements IGTWPacket {

    private String name;
    private int buyCost;
    private int rentCost;

    private int type;

    @Override
    public void fromBytes(ByteBuf buf) {
        buyCost = buf.readInt();
        rentCost = buf.readInt();
        type = buf.readInt();

        byte[] nameBytes = new byte[buf.readInt()];
        buf.readBytes(nameBytes);
        name = new String(nameBytes, Charset.defaultCharset());
    }


    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(buyCost);
        buf.writeInt(rentCost);
        buf.writeInt(type);

        byte[] nameBytes = name.getBytes(Charset.defaultCharset());
        buf.writeInt(nameBytes.length);
        buf.writeBytes(nameBytes);
    }
}
