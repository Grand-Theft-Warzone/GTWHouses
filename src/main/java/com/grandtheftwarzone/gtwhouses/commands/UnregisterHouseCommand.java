package com.grandtheftwarzone.gtwhouses.commands;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.dao.House;
import com.grandtheftwarzone.gtwhouses.util.HouseUtils;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.command.CommandBase;
import me.phoenixra.atum.core.exceptions.NotificationException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UnregisterHouseCommand extends AtumSubcommand {

    protected UnregisterHouseCommand(GTWHouses plugin, @NotNull CommandBase parent) {
        super(plugin, "unregister", "house.houseadmin", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        if (args.isEmpty()) throw new NotificationException("Usage: " + getUsage());

        String houseName = args.get(0);

        House house = GTWHouses.getHouseDatabase().getHouseByName(houseName);
        if (house == null) throw new NotificationException("House not found.");

        if (!GTWHouses.getHouseDatabase().removeHouse(house.getId()))
            throw new NotificationException("Could not unregister house.");

        sender.sendMessage("House " + ChatColor.GOLD + houseName + ChatColor.RESET + " has been unregistered successfully!");
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Removes house without resetting blocks";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house unregister <house>";
    }
}
