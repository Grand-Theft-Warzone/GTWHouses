package com.grandtheftwarzone.gtwhouses.server.runnables;

import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.util.HouseUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

public class RentRunnable implements Runnable {
    @Override
    public void run() {
        GTWHouses plugin = GTWHouses.getInstance();
        Server server = plugin.getServer();

        plugin.getLogger().info("Checking rent...");

        GTWHouses.getManager().getHouses().forEach(house -> {
            if (!house.isRented()) return;
            OfflinePlayer player = server.getOfflinePlayer(house.getOwner());
            if (player == null) return;
            HouseUtils.handleRent(house, player);
        });
    }
}
