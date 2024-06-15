package com.grandtheftwarzone.gtwhouses.commands;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.HousesManager;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AdminPlayerHousesCommand extends AtumSubcommand {

    public AdminPlayerHousesCommand(GTWHouses plugin, AtumCommand parent) {
        super(plugin, "player", "house.houseadmin", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender commandSender, @NotNull List<String> args) throws NotificationException {
        if (args.isEmpty()) throw new NotificationException("Usage: " + getUsage());

        String playerName = args.get(0);

        Player admin = (Player) commandSender;

        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        if (target == null) throw new NotificationException("Player not found");

        List<String> houses = GTWHouses.getManager().getPlayerHouses().get(target.getUniqueId());
        if (houses == null || houses.isEmpty()) {
            admin.sendMessage(playerName + " doesn't own any houses");
            return;
        }

        admin.sendMessage(playerName + "'s houses: ");
        houses.forEach(admin::sendMessage);
        admin.sendMessage("Total houses: " + houses.size());
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull List<String> list) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Get a list of a player's houses";
    }

    @Override
    public @NotNull String getUsage() {
        return "/houses player <player>";
    }
}
