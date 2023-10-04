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

public class UnsellHouseCommand extends AtumSubcommand {
    public UnsellHouseCommand(GTWHouses plugin, AtumCommand parent) {
        super(plugin, "unsell", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        Player player = (Player) sender;

        if (args.isEmpty())
            throw new NotificationException("Usage: " + getUsage());

        String houseName = args.get(0);

        House house = HouseManager.getHouse(houseName);
        if (house == null)
            throw new NotificationException("House not found");

        if (!house.getOwner().equals(player.getUniqueId()))
            throw new NotificationException("You are not the owner of this house");

        house.setSellable(-1);
        player.sendMessage("House " + ChatColor.GOLD + houseName + ChatColor.RESET + " is no longer for sale");
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Makes a house not for sale";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house unsell <house>";
    }
}
