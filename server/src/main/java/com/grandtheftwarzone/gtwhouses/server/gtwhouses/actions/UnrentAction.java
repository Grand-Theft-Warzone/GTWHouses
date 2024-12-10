package com.grandtheftwarzone.gtwhouses.server.gtwhouses.actions;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.GTWHouseAction.InvalidActionException;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.util.HouseUtils;
import org.bukkit.entity.Player;

public class UnrentAction {

    public static void handle(House house, Player player) throws InvalidActionException {
        if (!house.isRentedByPlayer() || house.getRenter() != player.getUniqueId())
            throw new InvalidActionException("This house is not rented by you.");

        house.resetRent();
        GTWHouses.getHousesManager().save();
        HouseUtils.resetHouseBlocks(house, player.getServer());


        player.sendMessage("You are no longer renting this house. And its blocks have been reset.");
        GTWHouses.getLoginMessageSystem().storeMessage(house.getOwner(), "You house " + house.getName() + " is no longer being rented by " + player.getDisplayName() + ".");
    }
}