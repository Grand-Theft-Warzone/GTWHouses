package com.grandtheftwarzone.gtwhouses.server.gtwhouses.actions;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.GTWHouseAction.InvalidActionException;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class RentAction {

    public static void handle(House house, Player player) throws InvalidActionException {
        if (!house.isRentable()) throw new InvalidActionException(ChatColor.RED + "This house is not rentable.");
        if (house.isRentedByPlayer()) throw new InvalidActionException(ChatColor.RED + "This house is already rented.");
        if (house.isOwned() && house.getOwner().equals(player.getUniqueId()))
            throw new InvalidActionException(ChatColor.RED + "You can't rent your own house.");

        Economy economy = GTWHouses.getEconomy();
        if (!economy.has(player, house.getRentCost()))
            throw new InvalidActionException(ChatColor.RED + "You don't have enough money to rent this house.");

        OfflinePlayer owner = GTWHouses.getInstance().getServer().getOfflinePlayer(house.getOwner());
        if (owner == null) throw new InvalidActionException(ChatColor.RED + "The owner of this house is not found.");

        if (!economy.depositPlayer(owner, house.getRentCost()).transactionSuccess() || !economy.withdrawPlayer(player, house.getRentCost()).transactionSuccess())
            throw new InvalidActionException(ChatColor.RED + "Failed to rent the house.");

        house.setRenter(player.getUniqueId());
        house.startRent();
        GTWHouses.getHousesManager().save();

        player.sendMessage(ChatColor.GREEN + "You have rented the house " + ChatColor.GOLD + house.getName() + ChatColor.GREEN + " for " + ChatColor.GOLD + house.getRentCost() + ChatColor.GREEN + ".");

        if (owner.isOnline())
            owner.getPlayer().sendMessage(ChatColor.GREEN + "Your house " + ChatColor.GOLD + house.getName() + ChatColor.GREEN + " has been rented by " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + " for " + ChatColor.GOLD + house.getRentCost() + ChatColor.GREEN + ".");
        else
            GTWHouses.getLoginMessageSystem().storeMessage(owner.getUniqueId(), "Your house " + house.getName() + " has been rented by " + player.getName() + " for " + house.getRentCost() + ".");
    }
}
