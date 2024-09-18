package com.grandtheftwarzone.gtwhouses.server;

import com.grandtheftwarzone.gtwhouses.common.HouseActions;
import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.OpenGUIC2SPacket;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.HousesGUIS2CPacket;
import com.grandtheftwarzone.gtwhouses.server.network.GTWPacketHandler;
import com.grandtheftwarzone.gtwhouses.server.network.HousesPacketManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class GTWHouseCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        if (args.length == 0) {
            getHelp(sender);
            return true;
        }

        //TODO: Add this to the actions without house
        if (args[0].equalsIgnoreCase("gui")) {
            GTWHouses.getPacketManager().sendPacket((Player) sender, new HousesGUIS2CPacket(false, GTWHouses.getManager().getHouses()));
            return true;
        }

        HouseActions action = HouseActions.get(args[0]);
        if (action == null) {
            getHelp(sender);
            return true;
        }

        if ((action.isRequiresAdmin() && (!sender.hasPermission("gtwhouses.admin") || !sender.isOp()))
                || (!action.isRequiresAdmin() && !sender.hasPermission("gtwhouses.houseplayer"))
        ) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (action == HouseActions.Sell && args.length < 3) {
            sender.sendMessage("Usage: /house " + action.getName().toLowerCase() + " <house> <price>");
            return true;
        } else {
            if (action.isRequiresHouse() && args.length < 2) {
                sender.sendMessage("Usage: /house " + action.getName().toLowerCase() + " <house>");
                return true;
            }
        }

        String house = null;

        int[] price = new int[1];

        int lastArgIndex = args.length - 1;

        if (action == HouseActions.Sell) {
            try {
                price[0] = Integer.parseInt(args[args.length - 1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Price must be a number.");
                return true;
            }

            lastArgIndex = args.length - 2;
        }

        if (action.isRequiresHouse())
            house = String.join(" ", Arrays.copyOfRange(args, 1, lastArgIndex + 1));

        try {
            GTWHouseAction.handle(action, (Player) sender, house, price);
        } catch (GTWHouseAction.InvalidActionException e) {
            sender.sendMessage(e.getMessage());
        }

        return true;
    }

    private void getHelp(CommandSender sender) {
        sender.sendMessage("House command usage: ");

        sender.sendMessage("/house gui - Opens the house GUI.");

        for (HouseActions action : HouseActions.values()) {
            if (action.isRequiresAdmin() && (!sender.hasPermission("gtwhouses.admin") || !sender.isOp()))
                continue;

            if (action == HouseActions.Sell)
                sender.sendMessage("/house " + action.name().toLowerCase() + " <house> <price> - " + action.getDescription());
            else if (action.isRequiresHouse())
                sender.sendMessage("/house " + action.name().toLowerCase() + "  <house>  - " + action.getDescription());
            else
                sender.sendMessage("/house " + action.name().toLowerCase() + " - " + action.getDescription());

        }
    }
}
