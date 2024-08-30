package com.grandtheftwarzone.gtwhouses.server.actions;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.GTWHouseAction.InvalidActionException;
import com.grandtheftwarzone.gtwhouses.server.util.HouseUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.GOLD;

public class RemoveAction {

    public static void handle(House house, Player player) throws InvalidActionException {
        GTWHouses.getManager().removeHouse(house);
        GTWHouses.getManager().save();

        HouseUtils.resetHouseBlocks(house, player.getServer());

        player.sendMessage("House " + GOLD + house.getName() + ChatColor.RESET + " removed.");

        if (house.isOwned())
            GTWHouses.getLoginMessageSystem().storeMessage(house.getOwner(), "Your house " + house.getName() + " has been removed.");
        if (house.isRented())
            GTWHouses.getLoginMessageSystem().storeMessage(house.getRenter(), "The house " + house.getName() + " you rented has been removed.");
    }
}
