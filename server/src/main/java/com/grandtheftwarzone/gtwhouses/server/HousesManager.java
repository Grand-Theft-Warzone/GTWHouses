package com.grandtheftwarzone.gtwhouses.server;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.data.HouseBlock;
import com.grandtheftwarzone.gtwhouses.server.util.HouseUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;

public class HousesManager {
    private final HashMap<String, House> houses = new HashMap<>();
    private final HashMap<String, List<HouseBlock>> houseBlocks = new HashMap<>();

    // Helper maps for player data (calculated on save)
    @Getter
    private HashMap<UUID, List<String>> playerHouses = new HashMap<>();
    @Getter
    private ArrayList<HouseHolder> playerAmounts = new ArrayList<>();

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

    private void updatePlayerHelpers() {
        playerHouses.clear();
        playerAmounts.clear();

        HashMap<UUID, HouseHolder> holders = new HashMap<>();

        for (House house : houses.values()) {
            if (!house.isOwned()) continue;

            List<String> houseList = playerHouses.putIfAbsent(house.getOwner(), new ArrayList<>());
            if (houseList == null) houseList = new ArrayList<>();

            HouseHolder holder = holders.putIfAbsent(house.getOwner(), new HouseHolder(house.getOwner(), 0, 0));
            if (holder == null) holder = holders.get(house.getOwner());

            houseList.add(house.getName());
            holder.add(house);

            playerHouses.put(house.getOwner(), houseList);
        }

        playerAmounts.addAll(holders.values());
        playerAmounts.sort(Comparator.comparingDouble(HouseHolder::getValue).reversed());
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

        updatePlayerHelpers();
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

        updatePlayerHelpers();

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


    @Getter
    @AllArgsConstructor
    static
    public class HouseHolder {
        private final UUID player;
        private int count;
        private double value;

        private void add(House house) {
            count++;
            value += house.getBuyCost();
        }
    }
}
