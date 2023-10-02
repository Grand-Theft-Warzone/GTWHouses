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

public class PayRentCommand extends AtumSubcommand {

    public PayRentCommand(@NotNull GTWHouses plugin, @NotNull AtumCommand parent) {
        super(plugin, "pay", parent);
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
            if (rentDays < 1) throw new NotificationException(ChatColor.RED + "Rent days must be greater than 0.");
        } catch (Exception e) {
            throw new NotificationException(ChatColor.RED + "Rent days must be a valid number.");
        }


        House house = HouseManager.getHouse(houseName);
        if (house == null) throw new NotificationException("House not found.");
        if (!house.getOwner().equals(player.getUniqueId()))
            throw new NotificationException(ChatColor.RED + "You are not the owner of this house.");

        Server server = sender.getServer();
        World world = server.getWorld(house.getWorld());

        ProtectedRegion protectedRegion = GTWHouses.getInstance().getWorldGuardPlugin().getRegionManager(world).getRegion(house.getName());
        if (protectedRegion == null) throw new NotificationException("House region not found.");

        int remainingDays = HouseUtils.getRemainingRentDays(house);
        if (remainingDays < 0) remainingDays = 0;

        int totalRentDays = remainingDays + rentDays;

        if (totalRentDays > 7)
            throw new NotificationException(ChatColor.RED + "You can't pay rent for more than 7 days." + ChatColor.RESET + " Current rent days: " + ChatColor.YELLOW + remainingDays);

        double rentPrice = house.getRentPerDay() * totalRentDays;
        Economy economy = GTWHouses.getInstance().getEconomy();
        if (!economy.has(player, house.getRentPerDay() * rentDays))
            throw new NotificationException("You don't have " + ChatColor.GREEN + "$" + rentPrice + ChatColor.RESET + " to rent this house for " + rentDays + " days.");

        economy.withdrawPlayer(player, house.getRentPerDay() * rentDays);

        house.updateRent(totalRentDays);

        player.sendMessage("You've paid " + ChatColor.GREEN + "$" + rentPrice + ChatColor.RESET + " rent of the house " + ChatColor.GOLD + houseName + ChatColor.RESET + " for " + ChatColor.YELLOW + rentDays + ChatColor.RESET + " day" + (rentDays > 1 ? "s" : "") + ".");
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Pay the rent of a house for a number of days";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house pay <house> <days>";
    }
}
