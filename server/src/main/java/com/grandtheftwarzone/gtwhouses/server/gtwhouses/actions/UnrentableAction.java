package com.grandtheftwarzone.gtwhouses.server.gtwhouses.actions;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.GTWHouseAction.InvalidActionException;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Date;

public class UnrentableAction {
    public static void handle(House house, Player player) throws InvalidActionException {
        if (!player.getUniqueId().equals(house.getOwner()))
            throw new InvalidActionException("You are not the owner of this house");

        //Make the renter leave the house in 3 days
        if (house.isRentedByPlayer()) {
            if (house.isKicked()){
                player.sendMessage("The house renter has already been given 3 days to leave the house.");
                return;
            }

            house.setRentedAt(null);
            Date rentDueDate = new Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000); //3 days
            house.setRentDueDate(rentDueDate);
            house.setKicked(true);
            GTWHouses.getHousesManager().save();

            player.sendMessage("The house renter has been given 3 days to leave the house.");

            OfflinePlayer renter = GTWHouses.getInstance().getServer().getOfflinePlayer(house.getRenter());
            GTWHouses.getLoginMessageSystem().sendOrStore(renter, "You have 3 days to leave the house " + house.getName() + " before being kicked out.");
            return;
        }

        house.resetRent();
        player.sendMessage("House " + ChatColor.GOLD + house.getName() + ChatColor.RESET + " is no longer for rent");
    }
}