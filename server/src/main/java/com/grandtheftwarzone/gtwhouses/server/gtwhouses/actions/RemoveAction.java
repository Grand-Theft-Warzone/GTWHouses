package com.grandtheftwarzone.gtwhouses.server.gtwhouses.actions;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.GTWHouseAction.InvalidActionException;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.util.HouseUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.GOLD;

public class RemoveAction {

    public static void handle(House house, Player player) throws InvalidActionException {
        HouseUtils.resetHouseBlocks(house, player.getServer());

        GTWHouses.getHousesManager().removeHouse(house);
        GTWHouses.getHousesManager().save();

        player.sendMessage("House " + GOLD + house.getName() + ChatColor.RESET + " removed.");

        if (house.isOwned())
            GTWHouses.getLoginMessageSystem().storeMessage(house.getOwner(), "Your house " + house.getName() + " has been removed.");
        if (house.isRented())
            GTWHouses.getLoginMessageSystem().storeMessage(house.getRenter(), "The house " + house.getName() + " you rented has been removed.");
    }
}
