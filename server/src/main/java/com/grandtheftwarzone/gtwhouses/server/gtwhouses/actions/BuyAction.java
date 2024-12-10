package com.grandtheftwarzone.gtwhouses.server.gtwhouses.actions;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.GTWHouseAction;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.GTWHouseAction.InvalidActionException;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RESET;

public class BuyAction {

    public static void handle(House house, Player player) throws GTWHouseAction.InvalidActionException {
        if (!house.isForSale()) throw new InvalidActionException(ChatColor.RED + "This house is not for sale");

        double cost = house.isOwned() ? house.getSellCost() : house.getBuyCost();

        Economy economy = GTWHouses.getEconomy();
        if (!economy.has(player, cost))
            throw new InvalidActionException(ChatColor.RED + "You don't have " + GREEN + "$" + cost + RESET + " to buy this house");

        OfflinePlayer oldOwner = null;
        if (house.isOwned()) {
            if (house.getOwner().equals(player.getUniqueId()))
                throw new InvalidActionException("You already own this house");

            oldOwner = player.getServer().getOfflinePlayer(house.getOwner());
            if (oldOwner != null)
                economy.depositPlayer(oldOwner, cost);
        }

        economy.withdrawPlayer(player, cost);

        house.setOwner(player.getUniqueId());
        house.resetRent();
        GTWHouses.getHousesManager().save();

        player.sendMessage(GREEN + "You bought the house " + house.getName() + " for " + GREEN + "$" + cost);

        if (oldOwner != null) {
            if (oldOwner.isOnline())
                oldOwner.getPlayer().sendMessage(GREEN + "Your house " + house.getName() + " has been sold to " + player.getName() + " for " + GREEN + "$" + cost);
            else
                GTWHouses.getLoginMessageSystem().storeMessage(oldOwner.getUniqueId(), "Your house " + house.getName() + " has been sold to " + player.getName() + " for " + GREEN + "$" + cost);
        }
    }
}
