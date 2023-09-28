package com.edgarssilva.gtwhouses.manager;

import com.edgarssilva.gtwhouses.GTWHouses;
import com.edgarssilva.gtwhouses.util.HouseBlock;
import com.sk89q.worldedit.BlockVector;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class HouseManager {

    private static final HashMap<String, List<HouseBlock>> houseBlocks = new HashMap<>();

    public static boolean registerHouse(String houseName, List<HouseBlock> blocks) {
        if (houseBlocks.containsKey(houseName)) return false;
        houseBlocks.put(houseName, blocks);
        return true;
    }


    public static void load() {
        try {
            File file = new File(GTWHouses.CONFIG_FOLDER + "/houses.sav");
            if (!file.exists()) return;
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            HashMap<String, List<HouseBlock>> blocks = (HashMap<String, List<HouseBlock>>) objectInputStream.readObject();
            houseBlocks.clear();
            houseBlocks.putAll(blocks);

            objectInputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            GTWHouses.getInstance().getLogger().severe("Error while loading houses.sav file.");
        }
    }

    public static void save() {
        try {
            File file = new File(GTWHouses.CONFIG_FOLDER + "/houses.sav");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(houseBlocks);

            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            GTWHouses.getInstance().getLogger().severe("Error while saving houses.sav file.");
        }
    }


}
