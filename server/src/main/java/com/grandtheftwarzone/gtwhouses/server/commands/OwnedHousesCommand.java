package com.grandtheftwarzone.gtwhouses.server.commands;

import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OwnedHousesCommand extends AtumSubcommand {

    public OwnedHousesCommand(GTWHouses plugin, AtumCommand parent) {
        super(plugin, "owned", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender commandSender, @NotNull List<String> list) throws NotificationException {
        Player player = (Player) commandSender;

        List<String> houses = GTWHouses.getManager().getPlayerHouses().get(player.getUniqueId());
        if (houses == null || houses.isEmpty()) {
            player.sendMessage("You don't own any houses");
            return;
        }

        player.sendMessage("Your houses:");
        houses.forEach(player::sendMessage);
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull List<String> list) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "List of your houses";
    }

    @Override
    public @NotNull String getUsage() {
        return "/houses owned";
    }
}
