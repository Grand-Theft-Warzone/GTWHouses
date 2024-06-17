package com.grandtheftwarzone.gtwhouses.commands;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.HousesManager;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TopBuyersCommand extends AtumSubcommand {
    public TopBuyersCommand(GTWHouses plugin, AtumCommand parent) {
        super(plugin, "top", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender commandSender, @NotNull List<String> list) throws NotificationException {
        int count = Math.min(GTWHouses.getManager().getPlayerAmounts().size(), 10);

        commandSender.sendMessage("Top " + count + " house holders:");
        for (int i = 0; i < count; i++) {
            HousesManager.HouseHolder houseHolder = GTWHouses.getManager().getPlayerAmounts().get(i);

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(houseHolder.getPlayer());
            String value = String.format("%,d", houseHolder.getValue());
            commandSender.sendMessage((i + 1) + ". " + offlinePlayer.getName() + " - " + houseHolder.getCount() + " properties:  " + value + " value");
        }
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull List<String> list) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "List the top 10 house holders";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house top";
    }
}
