package com.grandtheftwarzone.gtwhouses.common.network.packets.c2s;

import com.grandtheftwarzone.gtwhouses.common.HouseActions;
import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HouseActionC2SPacket implements IGTWPacket {
    private HouseActions action;
    private String houseName;
    private int[] args;

    @Override
    public void fromBytes(ByteBuf buf) {
        action = HouseActions.values()[buf.readInt()];

        int houseNameLength = buf.readInt();
        byte[] houseNameBytes = new byte[houseNameLength];
        buf.readBytes(houseNameBytes);
        houseName = new String(houseNameBytes);

        int argsLength = buf.readInt();
        args = new int[argsLength];
        for (int i = 0; i < argsLength; i++) args[i] = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(action.ordinal());

        byte[] houseNameBytes = houseName.getBytes();
        buf.writeInt(houseNameBytes.length);
        buf.writeBytes(houseNameBytes);

        if (args == null) args = new int[0];
        buf.writeInt(args.length);
        for (int arg : args) buf.writeInt(arg);
    }
}
