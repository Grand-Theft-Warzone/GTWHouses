package com.grandtheftwarzone.gtwhouses.server;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.data.HousePlacedBlock;
import com.grandtheftwarzone.gtwhouses.server.util.HouseUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class HousesManager {
    private final HashMap<String, House> houses = new HashMap<>();
    private final HashMap<String, Map<Integer, HousePlacedBlock>> placedBlocks = new HashMap<>();

    //Helper list for images
    @Getter
    private final ArrayList<String> imagesURLs = new ArrayList<>();

    // Helper maps for player data (calculated on save)
    @Getter
    private HashMap<UUID, List<String>> playerHouses = new HashMap<>();
    @Getter
    private ArrayList<HouseHolder> playerAmounts = new ArrayList<>();

    private HouseSaver saver;

    public HousesManager() {
        saver = new HouseSaver(this);

        load();
    }

    public void addHouse(House house) {
        houses.put(house.getName(), house);
        placedBlocks.put(house.getName(), new HashMap<>());
    }

    public void removeHouse(House house) {
        removeHouse(house.getName());
    }

    public void removeHouse(String name) {
        houses.remove(name);
        placedBlocks.remove(name);
    }

    public boolean hasHouse(String name) {
        return houses.containsKey(name);
    }

    public void updateHouse(String originalName, House house ) {
        Map<Integer, HousePlacedBlock> blocks = placedBlocks.get(originalName);
        removeHouse(originalName);

        houses.put(house.getName(), house);
        placedBlocks.put(house.getName(), blocks);

        //TODO: Remove placed blocks that are not in the new house location
    }

    public Collection<House> getHouses() {
        return houses.values();
    }

    public House getHouse(String name) {
        return houses.get(name);
    }

    public boolean hasName(String houseName) {
        return houses.containsKey(houseName);
    }

    public House getHouseInLocation(Location location) {
        for (House house : houses.values())
            if (HouseUtils.isLocationInHouse(location, house)) return house;
        return null;
    }

    public Map<Integer, HousePlacedBlock> getPlacedBlocks(String houseName) {
        return placedBlocks.get(houseName);
    }

    private void updatePlayerHelpers() {
        playerHouses.clear();
        playerAmounts.clear();
        imagesURLs.clear();

        HashMap<UUID, HouseHolder> holders = new HashMap<>();

        for (House house : houses.values()) {
            if (house.getImageURL() != null && !house.getImageURL().isEmpty()) imagesURLs.add(house.getImageURL());

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
        placedBlocks.clear();

        try {
            List<HouseSaver.HouseAndBlock> loaded = saver.load();

            for (HouseSaver.HouseAndBlock houseAndBlock : loaded) {
                houses.put(houseAndBlock.getHouse().getName(), houseAndBlock.getHouse());
                placedBlocks.put(houseAndBlock.getHouse().getName(), houseAndBlock.getBlocks().stream().collect(Collectors.toMap(HousePlacedBlock::getHash, block -> block)));
            }

            updatePlayerHelpers();
            GTWHouses.getInstance().getLogger().info("Loaded " + houses.size() + " houses.");
        } catch (Exception e) {
            GTWHouses.getInstance().getLogger().severe("Error loading houses.sav");
            e.printStackTrace();
        }
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
        updatePlayerHelpers();

        List<HouseSaver.HouseAndBlock> data = houses.values().stream().map(house -> new HouseSaver.HouseAndBlock(house, placedBlocks.get(house.getName()).values())).collect(Collectors.toList());

        try {
            saver.save(data);
            GTWHouses.getInstance().getLogger().info("Saved houses.sav");
        } catch (Exception e) {
            GTWHouses.getInstance().getLogger().severe("Error saving houses.sav");
            e.printStackTrace();
        }
    }


    @Getter
    @AllArgsConstructor
    static public class HouseHolder {
        private final UUID player;
        private int count;
        private double value;

        private void add(House house) {
            count++;
            value += house.getBuyCost();
        }
    }
}
