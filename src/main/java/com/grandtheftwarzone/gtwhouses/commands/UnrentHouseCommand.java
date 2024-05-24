package com.grandtheftwarzone.gtwhouses.commands;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.pojo.House;
import com.grandtheftwarzone.gtwhouses.util.HouseUtils;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.command.AtumSubcommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UnrentHouseCommand extends AtumSubcommand {

    public UnrentHouseCommand(GTWHouses plugin, AtumCommand parent) {
        super(plugin, "unrent", parent);
    }

    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        Player player = (Player) sender;
        if (args.size() != 1) throw new NotificationException("Usage: " + getUsage());

        String houseName = args.get(0);
        House house = GTWHouses.getManager().getHouse(houseName);
        if (house == null) throw new NotificationException("House not found.");

        if (!house.isRentedByPlayer() || house.getRenter() != player.getUniqueId())
            throw new NotificationException("This house is not rented by you.");

        house.resetRent();
        GTWHouses.getManager().save();
        HouseUtils.resetHouseBlocks(house, sender.getServer());


        player.sendMessage("You are no longer renting this house. And its blocks have been reset.");
        GTWHouses.getLoginMessageSystem().storeMessage(house.getOwner(), "You house " + houseName + " is no longer being rented by " + player.getDisplayName() + ".");
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull List<String> list) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Stop renting a house";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house unrent <house>";
    }
}
