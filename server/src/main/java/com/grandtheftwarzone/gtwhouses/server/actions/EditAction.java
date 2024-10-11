package com.grandtheftwarzone.gtwhouses.server.actions;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.OpenGUIC2SPacket;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.HouseCoordsS2CPacket;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.HouseImagesS2CPacket;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.RegisterHouseS2CPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouseAction;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.handlers.CreateHouseHandler;
import com.grandtheftwarzone.gtwhouses.server.handlers.HouseCoordsHandler;
import com.grandtheftwarzone.gtwhouses.server.util.HouseUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class EditAction {

    public static void handle(House house, Player player) throws GTWHouseAction.InvalidActionException {
        if (house.isOwned())
            throw new GTWHouseAction.InvalidActionException(ChatColor.RED + "This house is currently owned and cannot be edited! \n" + ChatColor.GOLD + "(You can try to reset the house first)");
        if (house.isRented())
            throw new GTWHouseAction.InvalidActionException(ChatColor.RED + "This house is currently rented and cannot be edited!\n" + ChatColor.GOLD + " (You can try to reset the house first)");


        GTWHouses.getPacketManager().sendPacket(player, new HouseCoordsS2CPacket(
                house.getName(), house.getBuyCost(), house.getRentCost(), house.getType().ordinal(),
                house.getMaxPosX(), house.getMinPosY(), house.getMinPosZ(), house.getMaxPosX(), house.getMaxPosY(), house.getMaxPosZ(),
                house.getWorld(),
                house.getImageURL(),
                GTWHouses.getManager().getHouseBlocks(house.getName()).size(), house.getName()
        ));

        HouseCoordsHandler.houseBlocks.put(player.getUniqueId(), GTWHouses.getManager().getHouseBlocks(house.getName()));

        //TODO: Make this in a separate method and maybe thread
        for (Player p : GTWHouses.getInstance().getServer().getOnlinePlayers())
            GTWHouses.getPacketManager().sendPacket(p, new HouseImagesS2CPacket(Collections.singletonList(house.getImageURL())));

    }
}
