package com.grandtheftwarzone.gtwhouses.commands;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.dao.House;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RESET;

public class BuyHouseCommand extends AtumSubcommand {

    public BuyHouseCommand(GTWHouses plugin, AtumCommand parent) {
        super(plugin, "buy", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
            Player player = (Player) sender;

            if (args.size() != 1) throw new NotificationException("Usage: " + getUsage());

            House house = GTWHouses.getHouseDatabase().getHouseByName(args.get(0));

            if (house == null) throw new NotificationException("House not found");
            if (!house.isSellable()) throw new NotificationException("House is not for sale");


            Economy economy = GTWHouses.getEconomy();
            if (!economy.has(player, house.getCost()))
                throw new NotificationException("You don't have " + GREEN + "$" + house.getCost() + RESET + " to buy this house");

            if (house.isOwned()) {
                OfflinePlayer oldOwner = player.getServer().getOfflinePlayer(house.getOwner());
                if (oldOwner != null)
                    economy.depositPlayer(oldOwner, house.getCost());
            }

            economy.withdrawPlayer(player, house.getCost());

            double cost = house.getCost(); // Cost will be different after the update
            if (GTWHouses.getHouseDatabase().updateHouseOwner(house, player.getUniqueId()))
                player.sendMessage(GREEN + "You bought the house " + house.getName() + " for " + GREEN + "$" + cost);
            else throw new NotificationException("An error occurred while trying to buy the house");

            //TODO: SEND MESSAGE TO OLD OWNER
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Buy a house";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house buy <house>";
    }
}
