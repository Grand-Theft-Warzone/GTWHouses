package com.grandtheftwarzone.gtwhouses.database;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.util.House;
import com.grandtheftwarzone.gtwhouses.util.HouseUtils;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.bukkit.Location;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HouseCache {

    private final LoadingCache<Integer, House> houses;

    public HouseCache() {
        houses = Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(k -> GTWHouses.getHouseDatabase().getHouseById(k));

        houses.putAll(GTWHouses.getHouseDatabase().getHouses().stream().collect(Collectors.toMap(House::getId, house -> house)));
    }

    public Collection<House> getHouses() {
        return houses.asMap().values();
    }

    public House getHouseInLocation(Location location) {
        for (House house : houses.asMap().values()) {
            if (!HouseUtils.isLocationInHouse(location, house)) continue;
            return house;
        }
        return null;
    }

    public void updateHouse(House house) {
        houses.put(house.getId(), house);
    }

    public void refreshHouse(int id) {
        houses.refresh(id);
    }

    public void removeHouse(int id) {
        houses.invalidate(id);
    }
}
