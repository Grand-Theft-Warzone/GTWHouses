package com.grandtheftwarzone.gtwhouses.server.commands;

import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CitySellHouseCommand extends AtumSubcommand {
    public CitySellHouseCommand(GTWHouses plugin, AtumCommand parent) {
        super(plugin, "citysell", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        Player player = (Player) sender;

        if (args.size() != 1) {
            throw new NotificationException("You must specify a house");
        }

        String houseName = args.get(0);

//        House house = GTWHouses.getHouseDatabase().getHouseByName(houseName);
        House house = GTWHouses.getManager().getHouse(houseName);
        if (house == null) throw new NotificationException("House not found");

        if (!player.getUniqueId().equals(house.getOwner())) {
            throw new NotificationException("You are not the owner of this house");
        }

        if (house.isForSale()) {
            throw new NotificationException("This house is already for sale");
        }

        if (house.isRented()) {
            throw new NotificationException("This house is already rented. Make sure to cancel the rent first.");
        }

        double cost = house.getBuyCost() / 2;

        house.setUnowned();
        GTWHouses.getManager().save();

        GTWHouses.getEconomy().depositPlayer(player, cost);
        player.sendMessage("House " + ChatColor.GOLD + houseName + ChatColor.RESET + " has been sold to the city for " + cost);
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Sell a house to the city for half the buy cost";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house citysell <house>";
    }
}
