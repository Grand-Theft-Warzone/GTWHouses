package com.grandtheftwarzone.gtwhouses.server.actions;

import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import org.bukkit.entity.Player;

import java.util.List;

public class ListAction {

    public static void handle(Player player) {
        List<String> houses = GTWHouses.getManager().getPlayerHouses().get(player.getUniqueId());
        if (houses == null || houses.isEmpty()) {
            player.sendMessage("You don't own any houses");
            return;
        }

        player.sendMessage("Your houses:");
        houses.forEach(player::sendMessage);
    }
}
