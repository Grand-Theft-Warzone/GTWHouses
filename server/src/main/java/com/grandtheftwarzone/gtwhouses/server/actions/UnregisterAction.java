package com.grandtheftwarzone.gtwhouses.server.actions;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.GTWHouseAction.InvalidActionException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class UnregisterAction {
    public static void handle(House house, Player player) throws InvalidActionException {
        GTWHouses.getManager().removeHouse(house);
        GTWHouses.getManager().save();

        player.sendMessage("House " + ChatColor.GOLD + house.getName() + ChatColor.RESET + " has been unregistered successfully!");
    }
}