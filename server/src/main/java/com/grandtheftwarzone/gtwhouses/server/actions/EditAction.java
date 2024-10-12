package com.grandtheftwarzone.gtwhouses.server.actions;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.HouseCoordsS2CPacket;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.HouseImagesS2CPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouseAction;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.handlers.HouseCoordsHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;

public class EditAction {

    public static void handle(House house, Player player) throws GTWHouseAction.InvalidActionException {
        if (house.isOwned())
            throw new GTWHouseAction.InvalidActionException(ChatColor.RED + "This house is currently owned and cannot be edited! \n" + ChatColor.GOLD + "(You can try to reset the house first)");
        if (house.isRented())
            throw new GTWHouseAction.InvalidActionException(ChatColor.RED + "This house is currently rented and cannot be edited!\n" + ChatColor.GOLD + " (You can try to reset the house first)");


        GTWHouses.getPacketManager().sendPacket(player, new HouseCoordsS2CPacket(
                house.getName(), house.getBuyCost(), house.getRentCost(), house.getType().ordinal(),
                house.getMinPosX(), house.getMinPosY(), house.getMinPosZ(), house.getMaxPosX(), house.getMaxPosY(), house.getMaxPosZ(),
                house.getWorld(),
                house.getImageURL(),
                house.getName()
        ));
    }
}
