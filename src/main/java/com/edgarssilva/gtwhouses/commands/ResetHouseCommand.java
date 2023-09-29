package com.edgarssilva.gtwhouses.commands;

import com.edgarssilva.gtwhouses.GTWHouses;
import com.edgarssilva.gtwhouses.manager.HouseManager;
import com.edgarssilva.gtwhouses.util.House;
import com.edgarssilva.gtwhouses.util.HouseUtils;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.command.CommandBase;
import me.phoenixra.atum.core.exceptions.NotificationException;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResetHouseCommand extends AtumSubcommand {

    protected ResetHouseCommand(GTWHouses plugin, @NotNull CommandBase parent) {
        super(plugin, "reset", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        if (args.isEmpty()) throw new NotificationException("Usage: " + getUsage());

        String houseName = args.get(0);


        Server server = sender.getServer();

        House house = HouseManager.getHouse(houseName);
        if (house == null) throw new NotificationException("House not found.");

        World world = house.getBlocks().get(0).getWorld(server);

        try {
            RegionManager rm = GTWHouses.getInstance().getWorldGuardPlugin().getRegionManager(world);
            if (!HouseUtils.resetHouse(rm, house, world))
                throw new NotificationException("House region not found.");

        } catch (StorageException e) {
            throw new NotificationException("Error while saving region owner.");
        }

        sender.sendMessage("House " + houseName + " has been reset successfully!");
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Resets a house and its blocks";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house reset <house_name>";
    }
}
