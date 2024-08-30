package com.grandtheftwarzone.gtwhouses.server.actions;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.GTWHouseAction.InvalidActionException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CitySellAction {

    public static void handle(House house, Player player) throws InvalidActionException {
        if (!player.getUniqueId().equals(house.getOwner())) {
            throw new InvalidActionException("You are not the owner of this house");
        }

        if (house.isForSale()) {
            throw new InvalidActionException("This house is already for sale");
        }

        if (house.isRented()) {
            throw new InvalidActionException("This house is already rented. Make sure to cancel the rent first.");
        }

        double cost = house.getBuyCost() / 2;

        house.setUnowned();
        GTWHouses.getManager().save();

        GTWHouses.getEconomy().depositPlayer(player, cost);
        player.sendMessage("House " + ChatColor.GOLD + house.getName() + ChatColor.RESET + " has been sold to the city for " + cost);
    }
}
