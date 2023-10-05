package com.edgarssilva.gtwhouses.commands;

import com.edgarssilva.gtwhouses.GTWHouses;
import com.edgarssilva.gtwhouses.manager.HouseManager;
import com.edgarssilva.gtwhouses.util.House;
import com.edgarssilva.gtwhouses.util.HouseUtils;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BuyHouseCommand extends AtumSubcommand {

    public BuyHouseCommand(GTWHouses plugin, AtumCommand parent) {
        super(plugin, "buy", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        Player player = (Player) sender;

        if (args.size() != 1)
            throw new NotificationException("Usage: " + getUsage());

        String houseName = args.get(0);

        House house = HouseManager.getHouse(houseName);
        if (house == null)
            throw new NotificationException("House not found");

        if (!house.isSellable())
            throw new NotificationException("House is not for sale");

        double cost = house.getCost();

        Economy economy = GTWHouses.getInstance().getEconomy();
        if (!economy.has(player, cost))
            throw new NotificationException("You don't have " + ChatColor.GREEN + "$" + cost + ChatColor.RESET + " to buy this house");

        if (house.isOwned()) {
            OfflinePlayer oldOwner = player.getServer().getOfflinePlayer(house.getOwner());
            if (oldOwner != null)
                economy.depositPlayer(oldOwner, cost);
        }

        economy.withdrawPlayer(player, cost);

        house.setOwner(player.getUniqueId());
        HouseUtils.updateRegionPermissions(house);
        HouseManager.setDirty();
        player.sendMessage(ChatColor.GREEN + "You bought the house " + house.getName() + " for " + ChatColor.GREEN + "$" + cost);
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
