package com.grandtheftwarzone.gtwhouses.server.gtwhouses.actions;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.GTWHouseAction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditBlocksAction {
    public static final Map<UUID, House> editing = new HashMap<>();

    public static void handle(House house, Player player) throws GTWHouseAction.InvalidActionException {
        if (house.isOwned())
            throw new GTWHouseAction.InvalidActionException(ChatColor.RED + "This house is currently owned and cannot be edited! \n" + ChatColor.GOLD + "(You can try to reset the house first)");
        if (house.isRented())
            throw new GTWHouseAction.InvalidActionException(ChatColor.RED + "This house is currently rented and cannot be edited!\n" + ChatColor.GOLD + " (You can try to reset the house first)");

        if (editing.containsKey(player.getUniqueId())) {
            throw new GTWHouseAction.InvalidActionException("You are already editing a house!");
        }

        editing.put(player.getUniqueId(), house);

        player.sendMessage("You are now editing house " + house.getName());
        player.sendMessage("Use the command /house finish to finish editing the house");
    }


    public static void handleFinish(Player player) {
        if (!editing.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are not editing a house!");
            return;
        }

        House edited = editing.get(player.getUniqueId());
        editing.remove(player.getUniqueId());

        player.sendMessage("House " + edited.getName() + " has been edited!");
    }
}
