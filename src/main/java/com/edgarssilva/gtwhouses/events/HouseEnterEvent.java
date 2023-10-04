package com.edgarssilva.gtwhouses.events;

import com.edgarssilva.gtwhouses.MovementWay;
import com.edgarssilva.gtwhouses.events.wg_events.RegionEnteredEvent;
import com.edgarssilva.gtwhouses.manager.HouseManager;
import com.edgarssilva.gtwhouses.util.House;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
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

        if (house.getOwner().equals(event.getPlayer().getUniqueId()))
            message = "You entered your house " + ChatColor.GOLD + house.getName() + ".";
        else {
            Server server = event.getPlayer().getServer();
            OfflinePlayer owner = server.getOfflinePlayer(house.getOwner());
            if (owner == null) return;

            message = "You entered " + ChatColor.YELLOW + owner.getName() + ChatColor.RESET + "'s house.";

            if (house.isSellable())
                message += " It's for sale for " + ChatColor.GREEN + "$" + house.getSellCost() + ChatColor.RESET + ".";

            if (house.isRentable() && !house.getRent().isRented())
                message += "You can rent it for " + ChatColor.GREEN + "$" + house.getRent().getCostPerDay() + ChatColor.RESET + " per day.";
        }

        event.getPlayer().sendMessage(message);
    }
}
