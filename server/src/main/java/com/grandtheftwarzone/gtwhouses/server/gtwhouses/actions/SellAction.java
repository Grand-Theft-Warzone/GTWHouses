package com.grandtheftwarzone.gtwhouses.server.gtwhouses.actions;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.GTWHouseAction.InvalidActionException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SellAction {

    public static void handle(House house, Player player, int cost) throws InvalidActionException {
        if (!player.getUniqueId().equals(house.getOwner())) {
            throw new InvalidActionException("You are not the owner of this house");
        }

        if (house.isForSale()) {
            throw new InvalidActionException("This house is already for sale");
        }

        if (house.isRented()) {
            if (house.isKicked())
                throw new InvalidActionException("The renter has been kicked out. You must wait 3 days before making the house rentable again.");
            else throw new InvalidActionException("This house is already rented. Make sure to cancel the rent first.");
        }

        house.setSellCost(cost);
        GTWHouses.getHousesManager().save();

        player.sendMessage("House " + ChatColor.GOLD + house.getName() + ChatColor.RESET + " is now for sale for " + cost);
    }
}
