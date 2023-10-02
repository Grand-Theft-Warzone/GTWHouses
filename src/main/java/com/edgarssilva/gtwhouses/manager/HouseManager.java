package com.edgarssilva.gtwhouses.manager;

import com.edgarssilva.gtwhouses.GTWHouses;
import com.edgarssilva.gtwhouses.util.House;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HouseManager {
    private static final HashMap<String, House> houses = new HashMap<>();
    private static boolean isDirty = false;

    public static final Runnable SAVE_RUNNABLE = () -> {
        if (isDirty) save();
    };

    public static boolean registerHouse(House house) {
        if (houses.containsKey(house.getName())) return false;

        houses.put(house.getName(), house);
        isDirty = true;
        return true;
    }

    public static House getHouse(String houseName) {
        return houses.get(houseName);
    }

    public static List<House> getPlayerHouses(UUID player) {
        List<House> playerHouses = new ArrayList<>();

        houses.forEach((name, house) -> {
            if (house.getOwner() != null && house.getOwner().equals(player))
                playerHouses.add(house);
        });

        return playerHouses;
    }

    public static void removeHouse(String houseName) {
        houses.remove(houseName);
        isDirty = true;
    }

    public static boolean hasHouse(String id) {
        return houses.containsKey(id);
    }

    public static void load() {
        try {
            File file = new File(GTWHouses.CONFIG_FOLDER + "/houses.sav");
            if (!file.exists()) return;
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            HashMap<String, House> loadedHouses = (HashMap<String, House>) objectInputStream.readObject();
            houses.clear();
            houses.putAll(loadedHouses);


            objectInputStream.close();
            fileInputStream.close();

            GTWHouses.getInstance().getLogger().info("Loaded " + loadedHouses.size() + " houses!");
        } catch (Exception e) {
            GTWHouses.getInstance().getLogger().severe("Error while loading houses.sav file.");
        }
    }

    public static synchronized void save() {
        try {
            File file = new File(GTWHouses.CONFIG_FOLDER + "/houses.sav");
            Files.createDirectories(file.getParentFile().toPath());

            if (!file.exists()) file.createNewFile();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(houses);

            objectOutputStream.close();
            fileOutputStream.close();

            isDirty = false;
            GTWHouses.getInstance().getLogger().info("Saving houses...");
        } catch (Exception e) {
            GTWHouses.getInstance().getLogger().severe("Error while saving houses data!");

        }
    }


    public static void setDirty() {
        isDirty = true;
    }

}
