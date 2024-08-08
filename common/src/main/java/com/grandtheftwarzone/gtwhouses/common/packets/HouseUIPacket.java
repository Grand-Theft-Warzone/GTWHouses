package com.grandtheftwarzone.gtwhouses.common.packets;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

@AllArgsConstructor
public class HouseUIPacket implements IMessage {

    @Getter
    private House house;

    public HouseUIPacket() {
    }


    @Override
    public void fromBytes(ByteBuf buf) {
        if(buf.readableBytes() == 0) return;

        house = House.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (house != null) house.toBytes(buf);
    }
}
