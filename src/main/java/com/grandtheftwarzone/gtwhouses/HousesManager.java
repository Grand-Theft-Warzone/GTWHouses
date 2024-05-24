package com.grandtheftwarzone.gtwhouses;

import com.grandtheftwarzone.gtwhouses.pojo.House;
import com.grandtheftwarzone.gtwhouses.pojo.HouseBlock;
import com.grandtheftwarzone.gtwhouses.util.HouseUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;

public class HousesManager {
    private static final HashMap<String, House> houses = new HashMap<>();
    private static final HashMap<String, List<HouseBlock>> houseBlocks = new HashMap<>();

    private final File housesFile;
    private final FileConfiguration housesConfig;

    private final File houseBlocksFile;
    private final FileConfiguration houseBlocksConfig;

    public HousesManager() {
        housesFile = new File(GTWHouses.getInstance().getDataFolder(), "houses.yml");
        housesConfig = YamlConfiguration.loadConfiguration(housesFile);

        houseBlocksFile = new File(GTWHouses.getInstance().getDataFolder(), "blocks.yml");
        houseBlocksConfig = YamlConfiguration.loadConfiguration(houseBlocksFile);

        if (!housesFile.exists())
            GTWHouses.getInstance().saveResource("houses.yml", false);

        if (!houseBlocksFile.exists())
            GTWHouses.getInstance().saveResource("blocks.yml", false);

        load();
    }

    public void addHouse(House house, List<HouseBlock> blocks) {
        houses.put(house.getName(), house);
        houseBlocks.put(house.getName(), blocks);
    }

    public void removeHouse(House house) {
        houses.remove(house.getName());
        houseBlocks.remove(house.getName());
    }

    public Collection<House> getHouses() {
        return houses.values();
    }

    public House getHouse(String name) {
        return houses.get(name);
    }

    public List<HouseBlock> getHouseBlocks(String houseName) {
        return houseBlocks.get(houseName);
    }

    public boolean hasName(String houseName) {
        return houses.containsKey(houseName);
    }

    public House getHouseInLocation(Location location) {
        for (House house : houses.values())
            if (HouseUtils.isLocationInHouse(location, house))
                return house;
        return null;
    }

    public void load() {
        houses.clear();
        houseBlocks.clear();

        ConfigurationSection housesSection = housesConfig.getConfigurationSection("houses");
        if (housesSection == null) return;

        Set<String> houseNames = housesSection.getKeys(false);
        if (houseNames == null) return;

        for (String name : houseNames) {
            House house = (House) housesConfig.get("houses." + name);

            List<HouseBlock> blocks = (List<HouseBlock>) houseBlocksConfig.getList(name);

            houseBlocks.put(house.getName(), blocks);
            houses.put(house.getName(), house);
        }
        GTWHouses.getInstance().getLogger().info("Loaded " + houses.size() + " houses.");
    }

    private BukkitTask runningTask = null;

    public void save() {
        if (runningTask != null) runningTask.cancel();
        runningTask = new BukkitRunnable() {
            @Override
            public void run() {
                saveSync();
            }
        }.runTaskLaterAsynchronously(GTWHouses.getInstance(), 20L * 1L); //1 seconds
    }

    public void saveSync() {
        GTWHouses.getInstance().saveResource("blocks.yml", true);

        for (House house : houses.values()) {
            housesConfig.set("houses." + house.getName(), house);
            houseBlocksConfig.set(house.getName(), houseBlocks.get(house.getName()));
        }

        try {
            housesConfig.save(housesFile);
            houseBlocksConfig.save(houseBlocksFile);
            GTWHouses.getInstance().getLogger().info("Saved houses.yml and blocks.yml");
        } catch (Exception e) {
            GTWHouses.getInstance().getLogger().severe("Error saving houses.yml or blocks.yml");
            e.printStackTrace();
        }
    }

}
