package com.grandtheftwarzone.gtwhouses.common;

import com.grandtheftwarzone.gtwhouses.common.packets.HouseUIPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

@Getter
@NoArgsConstructor
public class GTWHousesPacket implements IMessage {
    private PacketType type;
    private IMessage packet;

    public GTWHousesPacket(IMessage packet) {
        this.type = PacketType.fromPacket(packet);
        this.packet = packet;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = PacketType.values()[buf.readInt()];

        if (type == PacketType.UNKNOWN) return;

        try {
            packet = type.clazz.newInstance();
            packet.fromBytes(buf);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.ordinal());
        packet.toBytes(buf);
    }

    public enum PacketType {
        HOUSE_UI(HouseUIPacket.class),

        UNKNOWN(null);
        //   HOUSE_UI_RESPONSE;

        public final Class<? extends IMessage> clazz;

        PacketType(Class<? extends IMessage> clazz) {
            this.clazz = clazz;
        }

        static PacketType fromPacket(IMessage packet) {
            for (PacketType type : values()) {
                if (type.clazz == packet.getClass()) {
                    return type;
                }
            }
            return UNKNOWN;
        }
    }
}
