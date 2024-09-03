package com.grandtheftwarzone.gtwhouses.server;

import com.grandtheftwarzone.gtwhouses.common.HouseActions;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.actions.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GTWHouseAction {
    public static void handle(HouseActions action, Player player, String houseName, int... args) throws InvalidActionException {
        if (action == null) return;

        if (!player.isOp() && action.isRequiresAdmin())
            throw new InvalidActionException(ChatColor.RED + "You do not have permission to perform this action");

        House house = GTWHouses.getManager().getHouse(houseName);
        if (house == null) throw new InvalidActionException(ChatColor.RED + "House not found");

        switch (action) {
            case Buy:
                BuyAction.handle(house, player);
                break;
            case Rent:
                RentAction.handle(house, player);
                break;
            case Unrent:
                UnrentAction.handle(house, player);
                break;
            case Sell:
                //TODO: Validate args
                SellAction.handle(house, player, args[0]);
                break;
            case CitySell:
                CitySellAction.handle(house, player);
                break;
            case Unsell:
                UnsellAction.handle(house, player);
                break;
            case Rentable:
                RentableAction.handle(house, player);
                break;
            case Unrentable:
                UnrentableAction.handle(house, player);
                break;

            //Admin
            case Remove:
                RemoveAction.handle(house, player);
                break;
            case Reset:
                ResetAction.handle(house, player);
                break;
            case Unregister:
                UnregisterAction.handle(house, player);
                break;
        }
    }

    public static class InvalidActionException extends Exception {
        public InvalidActionException(String message) {
            super(message);
        }
    }
}