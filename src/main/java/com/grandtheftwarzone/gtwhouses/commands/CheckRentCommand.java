package com.grandtheftwarzone.gtwhouses.commands;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.util.HouseUtils;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CheckRentCommand extends AtumSubcommand {

    public CheckRentCommand(@NotNull GTWHouses plugin, @NotNull AtumCommand parent) {
        super(plugin, "checkrent", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        Player player = (Player) sender;

        player.sendMessage("Checking rent status...");
        GTWHouses.getHouseDatabase().getPlayerRenterHouses(player.getUniqueId()).forEach(house -> {
            if (house.getRent() == null) return;
            HouseUtils.handleRent(house, player, true);
        });
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Check the rent status of your houses";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house checkrent";
    }
}
