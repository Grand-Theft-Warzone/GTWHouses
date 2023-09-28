package com.edgarssilva.gtwhouses.commands;

import com.edgarssilva.gtwhouses.GTWHouses;
import com.edgarssilva.gtwhouses.manager.HouseManager;
import com.edgarssilva.gtwhouses.util.HouseBlock;
import com.edgarssilva.gtwhouses.world_guard.GTWHousesFlagRegistry;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RegisterHouseCommand extends AtumSubcommand {
    private final GTWHouses plugin;

    protected RegisterHouseCommand(GTWHouses plugin, AtumCommand parent) {
        super(plugin, "register", parent);
        this.plugin = plugin;
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        Player player = (Player) sender;

        if (args.size() < 1) {
            throw new NotificationException("Usage: " + getUsage());
        }

        String houseName = "house_" + args.get(0);

//        com.sk89q.worldedit.entity.Player wePlayer = worldEditPlugin.wrapPlayer(player);
        Selection selection = plugin.getWorldEditPlugin().getSelection(player);

        if (selection == null) {
            throw new NotificationException("You must select a region first.");
        }

        RegionManager regionManager = plugin.getWorldGuardPlugin().getRegionManager(selection.getWorld());
        ProtectedCuboidRegion protectedRegion = new ProtectedCuboidRegion(houseName, selection.getNativeMinimumPoint().toBlockVector(), selection.getNativeMaximumPoint().toBlockVector());


        if (regionManager.hasRegion(houseName))
            throw new NotificationException("A house with this name already exists.");

        protectedRegion.getOwners().addPlayer(player.getUniqueId());

        protectedRegion.setFlag(DefaultFlag.BUILD, StateFlag.State.ALLOW);
        protectedRegion.setFlag(DefaultFlag.BUILD.getRegionGroupFlag(), RegionGroup.OWNERS);

        protectedRegion.setFlag(DefaultFlag.BLOCK_BREAK, StateFlag.State.DENY);
        protectedRegion.setFlag(DefaultFlag.BLOCK_BREAK.getRegionGroupFlag(), RegionGroup.ALL);

        protectedRegion.setFlag(DefaultFlag.BLOCK_PLACE, StateFlag.State.DENY);
        protectedRegion.setFlag(DefaultFlag.BLOCK_PLACE.getRegionGroupFlag(), RegionGroup.ALL);

        protectedRegion.setFlag(DefaultFlag.CHEST_ACCESS, StateFlag.State.DENY);
        protectedRegion.setFlag(DefaultFlag.CHEST_ACCESS.getRegionGroupFlag(), RegionGroup.NON_OWNERS);


        protectedRegion.setFlag(GTWHousesFlagRegistry.HOUSE, true);


        ArrayList<HouseBlock> houseBlocks = new ArrayList<>();
        Vector minPoint = protectedRegion.getMinimumPoint();
        Vector maxPoint = protectedRegion.getMaximumPoint();

        for (int x = minPoint.getBlockX(); x <= maxPoint.getBlockX(); x++)
            for (int y = minPoint.getBlockY(); y <= maxPoint.getBlockZ(); y++)
                for (int z = minPoint.getBlockZ(); z <= maxPoint.getBlockZ(); z++)
                    houseBlocks.add(new HouseBlock(player.getWorld(), x, y, z));


        regionManager.addRegion(protectedRegion);
        try {
            regionManager.save();

            if (HouseManager.registerHouse(houseName, houseBlocks))
                player.sendMessage("House registered successfully.");
            else throw new NotificationException("A house with this name already exists.");

        } catch (StorageException e) {
            throw new NotificationException("An error occurred while saving the region.");
        }
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Register a house with the selected region";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house register <house name>";
    }
}