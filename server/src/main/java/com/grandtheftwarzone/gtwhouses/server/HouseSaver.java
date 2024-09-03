package com.grandtheftwarzone.gtwhouses.server;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.data.HouseBlock;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.*;
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

        buffer.writeObject(hbs);

        buffer.flush();
        buffer.close();
    }

    public List<HouseAndBlock> load() throws Exception {
        if (!housesFile.exists()) {
            return Collections.emptyList();
        }

        FileInputStream reader = new FileInputStream(housesFile);
        ObjectInputStream buffer = new ObjectInputStream(reader);

        List<HouseAndBlock> hb = (List<HouseAndBlock>) buffer.readObject();

        buffer.close();

        return hb;
    }

    @Getter
    @AllArgsConstructor
    public static class HouseAndBlock implements Serializable {
        private House house;
        private List<HouseBlock> blocks;
    }
}
