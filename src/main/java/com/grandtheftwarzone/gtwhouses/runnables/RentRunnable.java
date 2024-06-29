package com.grandtheftwarzone.gtwhouses.runnables;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.util.HouseUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

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
