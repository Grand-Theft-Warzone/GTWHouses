package com.edgarssilva.gtwhouses.commands;

import com.edgarssilva.gtwhouses.GTWHouses;
import com.edgarssilva.gtwhouses.util.HouseUtils;
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
        HouseUtils.handleRent(player, true);
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
