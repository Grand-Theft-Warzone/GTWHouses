package com.grandtheftwarzone.gtwhouses.commands;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.dao.House;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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

        if (!player.getUniqueId().equals(house.getOwner()))
            throw new NotificationException("You are not the owner of this house");

        //Make the renter leave the house in 3 days
        if (house.isRentedByPlayer()) {
            if (!GTWHouses.getHouseDatabase().startKicking(house))
                throw new NotificationException("Failed to \"kick\" the renter!");
            player.sendMessage("The house renter has been given 3 days to leave the house.");

            OfflinePlayer renter = GTWHouses.getInstance().getServer().getOfflinePlayer(house.getRenter());
            GTWHouses.getLoginMessageSystem().sendOrStore(renter, "You have 3 days to leave the house " + house.getName() + " before being kicked out.");
            return;
        }

        if (GTWHouses.getHouseDatabase().stopRent(house))
            player.sendMessage("House " + ChatColor.GOLD + houseName + ChatColor.RESET + " is no longer for rent");
        else throw new NotificationException("Failed to make house un-rentable");
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
