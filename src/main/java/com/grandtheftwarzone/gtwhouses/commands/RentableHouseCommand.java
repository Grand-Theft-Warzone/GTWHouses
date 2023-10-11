package com.grandtheftwarzone.gtwhouses.commands;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.dao.House;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RentableHouseCommand extends AtumSubcommand {

    public RentableHouseCommand(GTWHouses plugin, AtumCommand parent) {
        super(plugin, "rentable", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        Player player = (Player) sender;

        if (args.size() < 2)
            throw new NotificationException("Usage: " + getUsage());

        String houseName = args.get(0);
        double costPerDay;
        try {
            costPerDay = Double.parseDouble(args.get(1));
            if (costPerDay < 0)
                throw new NotificationException("Cost per day must be a positive number");
        } catch (NumberFormatException e) {
            throw new NotificationException("Cost per day must be a valid number");
        }

        House house = GTWHouses.getHouseDatabase().getHouseByName(houseName);

        if (house == null) throw new NotificationException("House not found");

        if (!player.getUniqueId().equals(house.getOwner()))
            throw new NotificationException("You are not the owner of this house");

        if (house.getRent() != null && house.getRent().isRented())
            throw new NotificationException("This house is already rented. Wait until the rent expires");

        if (GTWHouses.getHouseDatabase().setRentable(house, costPerDay))
            player.sendMessage("House " + ChatColor.GOLD + houseName + ChatColor.RESET + " is available for rent for " + ChatColor.GREEN + "$" + costPerDay + ChatColor.RESET + " per day");
        else throw new NotificationException("Could not set house as rentable");
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Makes a house available for rent";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house rent <house> <cost per day>";
    }
}
