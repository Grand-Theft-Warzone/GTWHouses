package com.edgarssilva.gtwhouses.events;

import com.edgarssilva.gtwhouses.GTWHouses;
import com.edgarssilva.gtwhouses.MovementWay;
import com.edgarssilva.gtwhouses.events.wg_events.RegionEnterEvent;
import com.edgarssilva.gtwhouses.events.wg_events.RegionEnteredEvent;
import com.edgarssilva.gtwhouses.manager.HouseManager;
import com.edgarssilva.gtwhouses.util.House;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HouseEnterEvent implements Listener {

    @EventHandler
    public void onHouseEnter(RegionEnteredEvent event) {
        if (event.getMovementWay() != MovementWay.MOVE) return;

        ProtectedRegion region = event.getRegion();
        if (!HouseManager.hasHouse(region.getId())) return;

        House house = HouseManager.getHouse(region.getId());

        String message;

        if (house.getRentStatus().isRented()) {
            if (house.getOwner().equals(event.getPlayer().getUniqueId())) {
                message = "You entered your house.";
            } else {
                Server server = event.getPlayer().getServer();
                OfflinePlayer owner = server.getOfflinePlayer(house.getOwner());
                if (owner == null) return;

                message = "You entered " + ChatColor.YELLOW + owner.getName() + ChatColor.RESET + "'s house.";
            }
        } else
            message = "You entered the house " + ChatColor.GOLD + house.getName() + ChatColor.RESET + ", available for " + ChatColor.GREEN + "$" + house.getRentPerDay() + ChatColor.RESET + " per day.";

        event.getPlayer().sendMessage(message);
    }
}
