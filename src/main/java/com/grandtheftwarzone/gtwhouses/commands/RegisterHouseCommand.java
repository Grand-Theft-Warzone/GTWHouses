package com.grandtheftwarzone.gtwhouses.commands;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.pojo.House;
import com.grandtheftwarzone.gtwhouses.pojo.HouseBlock;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RegisterHouseCommand extends AtumSubcommand {
    protected RegisterHouseCommand(GTWHouses plugin, AtumCommand parent) {
        super(plugin, "register", "house.houseadmin", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        Player player = (Player) sender;

        if (args.size() < 3) {
            throw new NotificationException("Usage: " + getUsage());
        }

        String houseName = args.get(0);
        String rentArg = args.get(1);
        String buyCostArg = args.get(2);

        double rent;
        double buyCost;
        try {
            rent = Double.parseDouble(rentArg);
            if (rent <= 0) throw new NotificationException("Rent must be greater than 0.");

            buyCost = Double.parseDouble(buyCostArg);
            if (buyCost <= 0) throw new NotificationException("Buy cost must be greater than 0.");
        } catch (NumberFormatException e) {
            throw new NotificationException("Rent and buy cost must be a valid numbers.");
        }

        Selection selection = GTWHouses.getWorldEditPlugin().getSelection(player);
        if (selection == null) throw new NotificationException("You must select a region first.");

        if (GTWHouses.getManager().hasName(houseName))
            throw new NotificationException("A house with this name already exists.");


        ArrayList<HouseBlock> houseBlocks = new ArrayList<>();
        org.bukkit.util.Vector minPoint = selection.getMinimumPoint().toVector();
        org.bukkit.util.Vector maxPoint = selection.getMaximumPoint().toVector();

        World world = player.getWorld();

        int minX = minPoint.getBlockX();
        int minY = minPoint.getBlockY();
        int minZ = minPoint.getBlockZ();
        int maxX = maxPoint.getBlockX();
        int maxY = maxPoint.getBlockY();
        int maxZ = maxPoint.getBlockZ();

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                for (int z = minZ; z <= maxZ; z++)
                    if (world.getBlockAt(x, y, z).getType() != Material.AIR)
                        houseBlocks.add(new HouseBlock(x, y, z));

        GTWHouses.getManager().addHouse(new House(houseName, world.getName(), minPoint, maxPoint, rent, buyCost), houseBlocks);
        GTWHouses.getManager().save();
        player.sendMessage(ChatColor.GREEN + "House registered successfully.");
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
        return "/house register <house name> <rent per day> <buy cost>";
    }
}