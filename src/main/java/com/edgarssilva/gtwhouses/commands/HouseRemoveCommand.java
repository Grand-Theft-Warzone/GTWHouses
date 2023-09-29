package com.edgarssilva.gtwhouses.commands;

import com.edgarssilva.gtwhouses.GTWHouses;
import com.edgarssilva.gtwhouses.manager.HouseManager;
import com.edgarssilva.gtwhouses.util.House;
import com.edgarssilva.gtwhouses.util.HouseUtils;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HouseRemoveCommand extends AtumSubcommand {

    public HouseRemoveCommand(GTWHouses plugin, AtumCommand parent) {
        super(plugin, "remove", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        Player player = (Player) sender;

        if (args.isEmpty()) throw new NotificationException("Usage: " + getUsage());

        String houseName = args.get(0);

        House house = HouseManager.getHouse(houseName);
        if (house == null) throw new NotificationException("House not found.");

        RegionManager rm = GTWHouses.getInstance().getWorldGuardPlugin().getRegionManager(player.getWorld());

        try {
            HouseUtils.resetHouse(rm, house, player.getWorld());
        } catch (StorageException e) {
           throw new NotificationException("An error occurred while removing the house blocks.");
        }

        rm.removeRegion(houseName);
        HouseManager.removeHouse(houseName);

        player.sendMessage("House " + ChatColor.GOLD + houseName + ChatColor.RESET + " removed.");
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Removes a house";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house remove <house_name>";
    }
}
