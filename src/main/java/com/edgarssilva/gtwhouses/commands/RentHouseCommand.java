package com.edgarssilva.gtwhouses.commands;

import com.edgarssilva.gtwhouses.GTWHouses;
import com.edgarssilva.gtwhouses.manager.HouseManager;
import com.edgarssilva.gtwhouses.util.House;
import com.edgarssilva.gtwhouses.util.HouseUtils;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RentHouseCommand extends AtumSubcommand {

    public RentHouseCommand(GTWHouses plugin, @NotNull AtumCommand parent) {
        super(plugin, "rent", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        Player player = (Player) sender;

        if (args.size() < 2) throw new NotificationException("Usage: " + getUsage());

        String houseName = args.get(0);
        String rentDaysArg = args.get(1);

        int rentDays;
        try {
            rentDays = Integer.parseInt(rentDaysArg);
            if (rentDays < 1) throw new NotificationException("Rent days must be greater than 0.");
        } catch (NumberFormatException e) {
            throw new NotificationException("Rent days must be a valid number.");
        }

        if (rentDays > 7) throw new NotificationException("You can't rent a house for more than 7 days.");

        House house = HouseManager.getHouse(houseName);
        if (house == null) throw new NotificationException(ChatColor.RED + "House not found.");

        if (house.getRentStatus() != HouseUtils.RentStatus.NOT_RENTED)
            throw new NotificationException(ChatColor.RED + "House is already rented.");

        Server server = sender.getServer();
        World world = server.getWorld(house.getWorld());

        ProtectedRegion protectedRegion = GTWHouses.getInstance().getWorldGuardPlugin().getRegionManager(world).getRegion(house.getName());
        if (protectedRegion == null) throw new NotificationException("House region not found.");

        double rentPrice = house.getRentPerDay() * rentDays;
        Economy economy = GTWHouses.getInstance().getEconomy();
        if (!economy.has(player, rentPrice))
            throw new NotificationException("You don't have " + ChatColor.GREEN + "$" + rentPrice + ChatColor.RESET + " to rent this house for " + rentDays + " days.");

        economy.withdrawPlayer(player, house.getRentPerDay() * rentDays);

        protectedRegion.getOwners().addPlayer(player.getUniqueId());
        house.setOwner(player.getUniqueId(), rentDays);


        player.sendMessage("You have rented the house " + ChatColor.GOLD + houseName + ChatColor.RESET + " for " + ChatColor.YELLOW + rentDays + ChatColor.RESET + " day" + (rentDays > 1 ? "s" : "") + ".");
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Rents a house for a given amount of days";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house rent <house_name> <rent_days>";
    }
}
