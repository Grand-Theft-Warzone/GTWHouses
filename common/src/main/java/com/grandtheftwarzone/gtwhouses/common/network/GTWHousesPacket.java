package com.grandtheftwarzone.gtwhouses.common.network;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

@Getter
@NoArgsConstructor
public class GTWHousesPacket implements IMessage {
    private Class<? extends IGTWPacket> clazz;
    private IGTWPacket packet;

    public GTWHousesPacket(IGTWPacket packet) {
        this.clazz = packet.getClass();
        this.packet = packet;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        byte[] classNameBytes = new byte[buf.readInt()];
        buf.readBytes(classNameBytes);

        String className = new String(classNameBytes);
        try {
            clazz = Class.forName(className).asSubclass(IGTWPacket.class);

            packet = clazz.newInstance();
            packet.fromBytes(buf);
        } catch (ClassNotFoundException e) {
            clazz = null;
        } catch (InstantiationException | IllegalAccessException e) {
            packet = null;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] classBytes = clazz.getName().getBytes();
        buf.writeInt(classBytes.length);
        buf.writeBytes(classBytes);

        packet.toBytes(buf);
    }

    public boolean isUnknown() {
        return clazz == null || packet == null;
    }
}
