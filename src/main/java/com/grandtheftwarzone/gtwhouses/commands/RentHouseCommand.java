package com.grandtheftwarzone.gtwhouses.commands;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.dao.House;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RentHouseCommand extends AtumSubcommand {

    public RentHouseCommand(GTWHouses plugin, AtumCommand parent) {
        super(plugin, "rent", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        Player player = (Player) sender;

        if (args.size() != 1) throw new NotificationException("Usage: " + getUsage());

        String houseName = args.get(0);

        House house = GTWHouses.getHouseDatabase().getHouseByName(houseName);
        if (house == null) throw new NotificationException(ChatColor.RED + "House not found.");

        if (!house.isRentable()) throw new NotificationException(ChatColor.RED + "This house is not rentable.");
        if (house.isRentedByPlayer()) throw new NotificationException(ChatColor.RED + "This house is already rented.");

        Economy economy = GTWHouses.getEconomy();
        if (!economy.has(player, house.getRentCost()))
            throw new NotificationException(ChatColor.RED + "You don't have enough money to rent this house.");

        OfflinePlayer owner = GTWHouses.getInstance().getServer().getOfflinePlayer(house.getOwner());
        if (owner == null) throw new NotificationException(ChatColor.RED + "The owner of this house is not found.");


        if (!economy.depositPlayer(owner, house.getRentCost()).transactionSuccess() || !economy.withdrawPlayer(player, house.getRentCost()).transactionSuccess())
            throw new NotificationException(ChatColor.RED + "Failed to rent the house.");

        house.setRenter(player.getUniqueId());
        if (!GTWHouses.getHouseDatabase().startRent(house))
            throw new NotificationException(ChatColor.RED + "Failed to rent the house.");

        player.sendMessage(ChatColor.GREEN + "You have rented the house " + ChatColor.GOLD + houseName + ChatColor.GREEN + " for " + ChatColor.GOLD + house.getRentCost() + ChatColor.GREEN + ".");

        if (owner.isOnline())
            owner.getPlayer().sendMessage(ChatColor.GREEN + "Your house " + ChatColor.GOLD + houseName + ChatColor.GREEN + " has been rented by " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + " for " + ChatColor.GOLD + house.getRentCost() + ChatColor.GREEN + ".");
        else GTWHouses.getLoginMessageSystem().storeMessage(owner.getUniqueId(), "Your house " + houseName + " has been rented by " + player.getName() + " for " + house.getRentCost() + ".");
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull List<String> list) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Rent a house, paying the rent to the owner daily.";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house rent <house>";
    }
}
