package com.grandtheftwarzone.gtwhouses.server.actions;

import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.HousesManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TopAction {

    public static void handle(Player player) {
        int count = Math.min(GTWHouses.getManager().getPlayerAmounts().size(), 10);

        player.sendMessage("Top " + count + " house holders:");
        for (int i = 0; i < count; i++) {
            HousesManager.HouseHolder houseHolder = GTWHouses.getManager().getPlayerAmounts().get(i);

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(houseHolder.getPlayer());
            String value = String.format("%,.2f", houseHolder.getValue());
            player.sendMessage((i + 1) + ". " + offlinePlayer.getName() + " - " + houseHolder.getCount() + " properties:  " + value + " value");
        }

    }
}
