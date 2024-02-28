package com.grandtheftwarzone.gtwhouses.commands;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.dao.House;
import com.grandtheftwarzone.gtwhouses.util.HouseUtils;
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

        House house = GTWHouses.getHouseDatabase().getHouseByName(houseName);
        if (house == null) throw new NotificationException("House not found.");

        if (!GTWHouses.getHouseDatabase().removeHouse(house.getId()))
            throw new NotificationException("An error occurred while removing the house.");

        HouseUtils.resetHouseBlocks(house, player.getServer());

        player.sendMessage("House " + GOLD + houseName + ChatColor.RESET + " removed.");
        //TODO: Add message to the house owner and renter
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
