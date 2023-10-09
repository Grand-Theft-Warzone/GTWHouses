package com.grandtheftwarzone.gtwhouses.commands;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.dao.House;
import com.grandtheftwarzone.gtwhouses.util.HouseUtils;
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

public class PayRentCommand extends AtumSubcommand {

    public PayRentCommand(@NotNull GTWHouses plugin, @NotNull AtumCommand parent) {
        super(plugin, "pay", parent);
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
            if (rentDays < 1 || rentDays > 7)
                throw new NotificationException(ChatColor.RED + "Rent days must be greater than 0 and smaller than 7.");
        } catch (NumberFormatException e) {
            throw new NotificationException(ChatColor.RED + "Rent days must be a valid number.");
        }


        House house = GTWHouses.getHouseDatabase().getHouseByName(houseName);
        if (house == null) throw new NotificationException("House not found.");

        if (!house.isRentable()) throw new NotificationException(ChatColor.RED + "This house is not rentable.");

        if (!house.getRent().getRenter().equals(player.getUniqueId()))
            throw new NotificationException(ChatColor.RED + "You are not paying rent for this house.");

        int remainingDays = HouseUtils.getRemainingRentDays(house.getRent());
        if (remainingDays < 0) remainingDays = 0;

        int totalRentDays = remainingDays + rentDays;

        if (totalRentDays > 7)
            throw new NotificationException(ChatColor.RED + "You can't pay rent for more than 7 days." + ChatColor.RESET + " Current rent days: " + ChatColor.YELLOW + remainingDays);

        double rentPrice = house.getRent().getCostPerDay() * totalRentDays;
        Economy economy = GTWHouses.getEconomy();
        if (!economy.has(player, house.getRent().getCostPerDay() * rentDays))
            throw new NotificationException("You don't have " + ChatColor.GREEN + "$" + rentPrice + ChatColor.RESET + " to rent this house for " + rentDays + " days.");


        OfflinePlayer owner = sender.getServer().getOfflinePlayer(house.getOwner());
        if (owner != null)
            economy.depositPlayer(owner, rentPrice);
        economy.withdrawPlayer(player, rentPrice);

        if (GTWHouses.getHouseDatabase().setPayedRent(house, rentDays))
            player.sendMessage("You've paid " + ChatColor.GREEN + "$" + rentPrice + ChatColor.RESET + " rent of the house " + ChatColor.GOLD + houseName + ChatColor.RESET + " for " + ChatColor.YELLOW + rentDays + ChatColor.RESET + " day" + (rentDays > 1 ? "s" : "") + ".");
        else throw new NotificationException("Failed to pay rent.");
        //TODO: Add message to the house owner
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Pay the rent of a house for a number of days";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house pay <house> <days>";
    }
}
