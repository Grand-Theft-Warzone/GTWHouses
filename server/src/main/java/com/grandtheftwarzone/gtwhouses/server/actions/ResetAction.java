package com.grandtheftwarzone.gtwhouses.server.actions;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.GTWHouseAction.InvalidActionException;
import com.grandtheftwarzone.gtwhouses.server.util.HouseUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ResetAction {

    public static void handle(House house, CommandSender sender) throws InvalidActionException {
        house.setOwner(null);
        house.resetRent();
        GTWHouses.getManager().save();

        HouseUtils.resetHouseBlocks(house, sender.getServer());
        sender.sendMessage("House " + ChatColor.GOLD + house.getName() + ChatColor.RESET + " has been reset successfully!");
    }
}