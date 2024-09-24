package com.grandtheftwarzone.gtwhouses.common.network.packets.s2c;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.OpenGUIC2SPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HousesGUIS2CPacket implements IGTWPacket {

    public OpenGUIC2SPacket.OpenGUIType type;
    private Collection<House> houses;

    @Override
    public void fromBytes(ByteBuf buf) {
        type = OpenGUIC2SPacket.OpenGUIType.values()[buf.readInt()];
        houses = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            House house = House.fromBytes(buf);
            houses.add(house);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.ordinal());
        buf.writeInt(houses.size());
        for (House house : houses) {
            house.toBytes(buf);
        }
    }
}
