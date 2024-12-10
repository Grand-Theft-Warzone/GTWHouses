package com.grandtheftwarzone.gtwhouses.server.gtwhouses;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.data.HousePlacedBlock;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class HouseSaver {
    private final HousesManager manager;
    private final File housesFile;

    public HouseSaver(HousesManager manager) {
        this.manager = manager;
        housesFile = new File(GTWHouses.getInstance().getDataFolder(), GTWHouses.houseSaveName);
    }

    public void save(List<HouseAndBlock> hbs) throws Exception {
        FileOutputStream writer = new FileOutputStream(housesFile);
        ObjectOutputStream buffer = new ObjectOutputStream(writer);

        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(hbs.size());
        for (HouseAndBlock hb : hbs) {
            hb.toBytes(buf);
        }
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        buffer.writeObject(bytes);


        buffer.flush();
        buffer.close();
    }

    public List<HouseAndBlock> load() throws Exception {
        if (!housesFile.exists()) {
            return Collections.emptyList();
        }

        FileInputStream reader = new FileInputStream(housesFile);
        ObjectInputStream buffer = new ObjectInputStream(reader);

        byte[] bytes = (byte[]) buffer.readObject();
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);

        List<HouseAndBlock> hb = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            hb.add(HouseAndBlock.fromByes(buf));
        }

        buffer.close();

        return hb;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HouseAndBlock implements Serializable {
        private House house;
        private Collection<HousePlacedBlock> blocks;

        public static HouseAndBlock fromByes(ByteBuf buf) {
            HouseAndBlock hb = new HouseAndBlock();
            hb.house = House.fromBytes(buf);
            hb.blocks = new ArrayList<>();

            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                hb.blocks.add(HousePlacedBlock.fromBytes(buf));
            }

            return hb;
        }

        public void toBytes(ByteBuf buf) {
            house.toBytes(buf);

            buf.writeInt(blocks.size());
            for (HousePlacedBlock block : blocks) {
                block.toBytes(buf);
            }
        }
    }
}
