package com.edgarssilva.gtwhouses.events;

import com.edgarssilva.gtwhouses.GTWHouses;
import com.edgarssilva.gtwhouses.manager.HouseManager;
import com.edgarssilva.gtwhouses.util.House;
import com.edgarssilva.gtwhouses.util.HouseRent;
import com.edgarssilva.gtwhouses.util.HouseUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class PlayerLoginListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ArrayList<String> warnings = new ArrayList<>();
        for (House h : HouseManager.getPlayerRenterHouses(player.getUniqueId())) {
            if (!h.isRentable()) continue;
            HouseRent rent = h.getRent();
            if (rent.getStatus()  == HouseRent.RentStatus.RENTED || rent.getStatus() == HouseRent.RentStatus.NOT_RENTED)
                continue;

            warnings.add(h.getRent().getStatus().getWarning(h));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                warnings.forEach(player::sendMessage);
            }
        }.runTaskLater(GTWHouses.getInstance(), 20 * 3);
    }
}
