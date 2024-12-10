package com.grandtheftwarzone.gtwhouses.server.gtwhouses.actions;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.GTWHouseAction.InvalidActionException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RentableAction {

    public static void handle(House house, Player player) throws InvalidActionException {
        double costPerDay = house.getRentCost();

        if (!player.getUniqueId().equals(house.getOwner()))
            throw new InvalidActionException("You are not the owner of this house");

        if (house.isRented()) {
            if (house.isKicked())
                throw new InvalidActionException("The renter has been kicked out. You must wait 3 days before making the house rentable again.");
            else
                throw new InvalidActionException("This house is already rented.");
        }

        house.startRent();
        GTWHouses.getHousesManager().save();

        player.sendMessage("House " + ChatColor.GOLD + house.getName() + ChatColor.RESET + " is being rent for " + ChatColor.GREEN + "$" + costPerDay + ChatColor.RESET + " per day");
    }
}