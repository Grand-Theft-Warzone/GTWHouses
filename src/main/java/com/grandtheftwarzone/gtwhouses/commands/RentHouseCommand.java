package com.grandtheftwarzone.gtwhouses.commands;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.dao.House;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RentHouseCommand extends AtumSubcommand {

    public RentHouseCommand(GTWHouses plugin, @NotNull AtumCommand parent) {
        super(plugin, "rent", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        Player player = (Player) sender;

        if (args.size() < 2) throw new NotificationException("Usage: " + getUsage());

        String houseName = args.get(0);
        String rentDaysArg = args.get(1);

        int rentDays;
        try {
            rentDays = Integer.parseInt(rentDaysArg);
            if (rentDays < 1) throw new NotificationException("Rent days must be greater than 0.");
            if (rentDays > 7) throw new NotificationException("You can't rent a house for more than 7 days.");
        } catch (NumberFormatException e) {
            throw new NotificationException("Rent days must be a valid number.");
        }

        House house = GTWHouses.getHouseDatabase().getHouseByName(houseName);
        if (house == null) throw new NotificationException(ChatColor.RED + "House not found.");

        if (!house.isRentable()) throw new NotificationException(ChatColor.RED + "This house is not rentable.");

        if (house.getRent().isRented()) throw new NotificationException(ChatColor.RED + "House is already rented.");

        Server server = sender.getServer();

        double rentPrice = house.getRent().getCostPerDay() * rentDays;
        Economy economy = GTWHouses.getEconomy();
        if (!economy.has(player, rentPrice))
            throw new NotificationException("You don't have " + ChatColor.GREEN + "$" + rentPrice + ChatColor.RESET + " to rent this house for " + rentDays + " days.");

        OfflinePlayer offlinePlayer = server.getOfflinePlayer(house.getOwner());
        if (offlinePlayer != null) economy.depositPlayer(offlinePlayer, rentPrice);
        economy.withdrawPlayer(player, rentPrice);


        if (GTWHouses.getHouseDatabase().startRent(house, player.getUniqueId(), rentDays))
            player.sendMessage(
                    "You have rented the house " + ChatColor.GOLD + houseName + ChatColor.RESET
                            + " for " + ChatColor.YELLOW + rentDays + ChatColor.RESET + " day" + (rentDays > 1 ? "s" : "")
                            + " for " + ChatColor.GREEN + "$" + rentPrice + ChatColor.RESET + "."
            );
        else
            throw new NotificationException(ChatColor.RED + "An error occurred while renting the house. Please contact an administrator.");
        //TODO: Add message to the house owner
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Rents a house for a given amount of days";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house rent <house> <rent_days>";
    }
}
