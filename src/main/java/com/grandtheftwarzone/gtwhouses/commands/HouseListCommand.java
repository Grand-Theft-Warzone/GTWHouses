package com.grandtheftwarzone.gtwhouses.commands;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.dao.House;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HouseListCommand extends AtumSubcommand {

    public HouseListCommand(GTWHouses plugin, AtumCommand parent) {
        super(plugin, "list", "house.houseadmin", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender commandSender, @NotNull List<String> list) throws NotificationException {
        Player player = (Player) commandSender;

        List<House> houses = GTWHouses.getHouseDatabase().getHouses();

        if (houses.isEmpty()) {
            player.sendMessage("No houses found.");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Houses: ");
        stringBuilder.append("[");
        for (House house : houses) {
            stringBuilder.append(house.getName());
            stringBuilder.append(", ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("]");

        player.sendMessage(stringBuilder.toString());
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull List<String> list) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "List the names of the registered houses";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house list";
    }
}
