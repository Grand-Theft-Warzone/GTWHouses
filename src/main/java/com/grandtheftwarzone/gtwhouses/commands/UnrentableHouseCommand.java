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

public class UnrentableHouseCommand extends AtumSubcommand {

    public UnrentableHouseCommand(GTWHouses plugin, AtumCommand parent) {
        super(plugin, "unrentable", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        Player player = (Player) sender;

        if (args.isEmpty())
            throw new NotificationException("Usage: " + getUsage());

        String houseName = args.get(0);

        House house = GTWHouses.getHouseDatabase().getHouseByName(houseName);
        if (house == null) throw new NotificationException("House not found");

        if (!house.getOwner().equals(player.getUniqueId()))
            throw new NotificationException("You are not the owner of this house");

        if (GTWHouses.getHouseDatabase().setUnrentable(house))
            player.sendMessage("House " + ChatColor.GOLD + houseName + ChatColor.RESET + " is no longer for rent");
        else throw new NotificationException("Failed to make house un-rentable");
        //TODO: Add a message to the player who is renting the house
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Makes a house not available for rent";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house unrentable <house>";
    }
}
