package com.grandtheftwarzone.gtwhouses.runnables;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.util.HouseUtils;
import org.bukkit.Server;

public class RentRunnable implements Runnable {
    @Override
    public void run() {
        GTWHouses plugin = GTWHouses.getInstance();
        Server server = plugin.getServer();

        plugin.getLogger().info("Checking rent...");


        GTWHouses.getHouseCache().getHouses().forEach(house -> {
            if (!house.isRentable()) return;
            if (!house.getRent().isRented()) return;
            HouseUtils.handleRent(house, server.getPlayer(house.getRent().getRenter()), false);
        });
    }
}
