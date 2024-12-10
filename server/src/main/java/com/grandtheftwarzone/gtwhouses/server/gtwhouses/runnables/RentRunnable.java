package com.grandtheftwarzone.gtwhouses.server.gtwhouses.runnables;

import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.util.HouseUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

public class RentRunnable implements Runnable {
    @Override
    public void run() {
        GTWHouses plugin = GTWHouses.getInstance();
        Server server = plugin.getServer();

        plugin.getLogger().info("Checking rent...");

        GTWHouses.getHousesManager().getHouses().forEach(house -> {
            if (!house.isRented()) return;
            OfflinePlayer player = server.getOfflinePlayer(house.getOwner());
            if (player == null) return;
            HouseUtils.handleRent(house, player);
        });
    }
}
