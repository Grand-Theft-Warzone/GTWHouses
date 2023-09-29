package com.edgarssilva.gtwhouses.commands;

import com.edgarssilva.gtwhouses.GTWHouses;
import com.edgarssilva.gtwhouses.manager.HouseManager;
import com.edgarssilva.gtwhouses.util.House;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ListHousesCommand extends AtumSubcommand {

    public ListHousesCommand(GTWHouses plugin, AtumCommand parent) {
        super(plugin, "list", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        Player player = (Player) sender;

        List<House> houses = HouseManager.getPlayerHouses(player.getUniqueId());
        if (houses.isEmpty()) {
            player.sendMessage("You don't have any houses");
            return;
        }
        player.sendMessage("Your houses:");
        houses.forEach(house -> player.sendMessage(" - " + ChatColor.BOLD + ChatColor.GOLD + house.getName() + ChatColor.RESET + " with a rent of " + ChatColor.GREEN + house.getRent()));
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Lists all houses belonging to the player";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house list";
    }
}
