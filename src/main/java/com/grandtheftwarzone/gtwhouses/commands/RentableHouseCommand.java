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

        if (args.size() != 1)
            throw new NotificationException("Usage: " + getUsage());

        String houseName = args.get(0);
        House house = GTWHouses.getHouseDatabase().getHouseByName(houseName);
        if (house == null) throw new NotificationException("House not found");

        double costPerDay = house.getRentCost();

        if (!player.getUniqueId().equals(house.getOwner()))
            throw new NotificationException("You are not the owner of this house");

        if (house.isRented())
            throw new NotificationException("This house is already rented.");

        if (GTWHouses.getHouseDatabase().startRent(house))
            player.sendMessage("House " + ChatColor.GOLD + houseName + ChatColor.RESET + " is being rent for " + ChatColor.GREEN + "$" + costPerDay + ChatColor.RESET + " per day");
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
        return "/house rentable <house>";
    }
}
