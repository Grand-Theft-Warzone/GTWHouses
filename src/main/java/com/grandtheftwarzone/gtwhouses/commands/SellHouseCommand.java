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

public class SellHouseCommand extends AtumSubcommand {

    public SellHouseCommand(GTWHouses plugin, AtumCommand parent) {
        super(plugin, "sell", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        Player player = (Player) sender;

        if (args.size() < 2) {
            throw new NotificationException("You must specify a house and a cost");
        }

        String houseName = args.get(0);
        double cost;
        try {
            cost = Double.parseDouble(args.get(1));
        } catch (NumberFormatException e) {
            throw new NotificationException("The cost must be a number");
        }

        House house = GTWHouses.getHouseDatabase().getHouseByName(houseName);
        if (house == null) throw new NotificationException("House not found");


        if (!player.getUniqueId().equals(house.getOwner())) {
            throw new NotificationException("You are not the owner of this house");
        }

        if (house.isSellable()) {
            throw new NotificationException("This house is already for sale");
        }

        if (GTWHouses.getHouseDatabase().setSellable(house, cost))
            player.sendMessage("House " + ChatColor.GOLD + houseName + ChatColor.RESET + " is now for sale for " + cost);
        else
            throw new NotificationException("Could not set the house for sale");

//        if (house.isRentable())
        //TODO: Notifiy the player that the rent is not renewable
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Set a house for sale";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house sell <house> <cost>";
    }
}