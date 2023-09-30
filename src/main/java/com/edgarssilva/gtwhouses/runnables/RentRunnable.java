package com.edgarssilva.gtwhouses.runnables;

import com.edgarssilva.gtwhouses.GTWHouses;
import com.edgarssilva.gtwhouses.util.HouseUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

public class RentRunnable implements Runnable {
    @Override
    public void run() {
        GTWHouses plugin = GTWHouses.getInstance();
        Server server = plugin.getServer();

        plugin.getLogger().info("Checking rent...");

        for (OfflinePlayer p : server.getOfflinePlayers())
            HouseUtils.handleRent(p.getPlayer(), false);
    }
}
