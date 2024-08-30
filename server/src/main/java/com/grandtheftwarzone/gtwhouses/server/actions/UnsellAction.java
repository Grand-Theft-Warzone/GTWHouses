package com.grandtheftwarzone.gtwhouses.server.actions;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.GTWHouseAction.InvalidActionException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UnsellAction {
    public static void handle(House house, Player player) throws InvalidActionException {
        if (!player.getUniqueId().equals(house.getOwner()))
            throw new InvalidActionException("You are not the owner of this house");

        house.setSellCost(-1);
        GTWHouses.getManager().save();

        player.sendMessage("House " + ChatColor.GOLD + house.getName() + ChatColor.RESET + " is no longer for sale");
    }
}