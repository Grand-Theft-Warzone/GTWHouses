package com.grandtheftwarzone.gtwhouses.events;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.dao.House;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

public class HouseEnterEvent implements Listener {

    private final HashMap<UUID, Integer> playerHouse = new HashMap<>();
    private final HashMap<UUID, LastLeft> lastLefts = new HashMap<>();

    @EventHandler
    public void onHouseEnter(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location location = event.getTo();

        House house = GTWHouses.getHouseCache().getHouseInLocation(location);
        if (house == null) {
            if (!lastLefts.containsKey(player.getUniqueId()) && playerHouse.containsKey(player.getUniqueId())) {
                int id = playerHouse.remove(player.getUniqueId());
                lastLefts.put(player.getUniqueId(), new LastLeft(System.currentTimeMillis(), id));
            }
            return;
        }

        if (lastLefts.containsKey(player.getUniqueId())) {
            LastLeft lastLeft = lastLefts.get(player.getUniqueId());
            if (lastLeft.houseId == house.getId()) {
                if (System.currentTimeMillis() - lastLeft.time < 15000) return; // 15 seconds
                lastLefts.remove(player.getUniqueId());
            }
        }

        if (playerHouse.containsKey(player.getUniqueId()) && playerHouse.get(player.getUniqueId()) == house.getId())
            return;

        playerHouse.put(player.getUniqueId(), house.getId());

        String message;

        if (house.isOwned()) {
            if (event.getPlayer().getUniqueId().equals(house.getOwner()))
                message = "You entered your house " + ChatColor.GOLD + house.getName() + ".";
            else {
                Server server = event.getPlayer().getServer();
                OfflinePlayer owner = server.getOfflinePlayer(house.getOwner());
                if (owner == null) return;

                message = "You entered " + ChatColor.YELLOW + owner.getName() + ChatColor.RESET + "'s house " + ChatColor.GOLD + house.getName() + ChatColor.RESET + ".";

                if (house.isForSale())
                    message += " It's for sale for " + ChatColor.GREEN + "$" + house.getSellCost() + ChatColor.RESET + ".";

                if (house.isRentable() && !house.isRented())
                    message += " You can rent it for " + ChatColor.GREEN + "$" + house.getRentCost() + ChatColor.RESET + " per day.";
            }
        } else {
            message = "You entered house " + ChatColor.GOLD + house.getName() + ChatColor.RESET + " (unowned) you can buy it for " + ChatColor.GREEN + "$" + house.getBuyCost() + ChatColor.RESET + " and then rent it for " + ChatColor.GREEN + "$" + house.getRentCost() + ChatColor.RESET + " per day.";
        }

        player.sendMessage(message);
    }

    @AllArgsConstructor
    private static class LastLeft {
        final long time;
        final int houseId;
    }
}
