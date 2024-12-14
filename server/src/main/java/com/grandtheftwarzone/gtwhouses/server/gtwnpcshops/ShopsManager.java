package com.grandtheftwarzone.gtwhouses.server.gtwnpcshops;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.Shop;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItem;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.GTWSaver;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ShopsManager {
    private final GTWSaver<Shop> shopSaver;
    private final GTWSaver<ShopItem> itemSaver;

    @Getter
    private final HashMap<String, Shop> shops = new HashMap<>();
    @Getter
    private final HashMap<String, ShopItem> items = new HashMap<>();

    public ShopsManager() {
        shopSaver = new GTWSaver<>("shops.sav", Shop.class);
        itemSaver = new GTWSaver<>("items.sav", ShopItem.class);

        load();
    }

    public boolean hasShop(String name) {
        return shops.containsKey(name);
    }

    public Shop getShop(String name) {
        return shops.get(name);
    }

    public void setOrUpdateShop(Shop shop) {
        shops.put(shop.getName(), shop);
        save();
    }

    public ShopItem getItem(String name) {
        return items.get(name);
    }

    public boolean hasItem(String name) {
        return items.containsKey(name);
    }

    public void setOrUpdateItem(ShopItem item) {
        if (item.getBuyPrice() <= 0 && item.getSellPrice() <= 0) {
            items.remove(item.getItem());
        }

        if (item.getBuyPrice() <= 0) {
            shops.values().forEach(shop -> shop.getItems().remove(item.getItem()));
        } else {
            items.put(item.getItem(), item);
        }

        save();
    }

    public void removeShop(String name) {
        shops.remove(name);
        save();
    }

    public Collection<ShopItem> getShopItems(String shopName) {
        return shops.get(shopName).getItems().stream().map(this::getItem).collect(Collectors.toList());
    }


    public void load() {
        shops.clear();
        items.clear();

        try {
            shops.putAll(shopSaver.load().stream().collect(Collectors.toMap(Shop::getName, shop -> shop)));
            GTWHouses.getInstance().getLogger().info("Loaded " + shops.size() + " shops.");

            items.putAll(itemSaver.load().stream().collect(Collectors.toMap(ShopItem::getItem, item -> item)));
            GTWHouses.getInstance().getLogger().info("Loaded " + items.size() + " items.");
        } catch (Exception e) {
            GTWHouses.getInstance().getLogger().severe("Error loading shops and items");
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
        try {
            shopSaver.save(shops.values());
            GTWHouses.getInstance().getLogger().info("Saved shops.sav");
            itemSaver.save(items.values());
            GTWHouses.getInstance().getLogger().info("Saved items.sav");
        } catch (Exception e) {
            GTWHouses.getInstance().getLogger().severe("Error saving shops and items");
            e.printStackTrace();
        }
    }
}
