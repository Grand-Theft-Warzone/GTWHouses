package com.grandtheftwarzone.gtwhouses.server.commands;

import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.server.util.HouseUtils;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static org.bukkit.ChatColor.GOLD;

public class HouseRemoveCommand extends AtumSubcommand {

    public HouseRemoveCommand(GTWHouses plugin, AtumCommand parent) {
        super(plugin, "remove", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        Player player = (Player) sender;

        if (args.isEmpty()) throw new NotificationException("Usage: " + getUsage());

        String houseName = args.get(0);

//        House house = GTWHouses.getHouseDatabase().getHouseByName(houseName);
        House house = GTWHouses.getManager().getHouse(houseName);
        if (house == null) throw new NotificationException("House not found.");

        GTWHouses.getManager().removeHouse(house);
        GTWHouses.getManager().save();

        HouseUtils.resetHouseBlocks(house, player.getServer());

        player.sendMessage("House " + GOLD + houseName + ChatColor.RESET + " removed.");

        if (house.isOwned()) GTWHouses.getLoginMessageSystem().storeMessage(house.getOwner(), "Your house " + houseName + " has been removed.");
        if (house.isRented()) GTWHouses.getLoginMessageSystem().storeMessage(house.getRenter(), "The house " + houseName + " you rented has been removed.");
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Removes a house and resets its blocks";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house remove <house>";
    }
}