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

        if (args[0].equalsIgnoreCase("gui")) {
            GTWHouses.getPacketManager().sendPacket((Player) sender, new HousesGUIS2CPacket(GTWHouses.getManager().getHouses()));
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
        } else if (args.length < 2) {
            sender.sendMessage("Usage: /house " + action.getName().toLowerCase() + " <house>");
            return true;
        }

        String house = args[1];

        int[] price = new int[1];
        if (action == HouseActions.Sell) {
            try {
                price[0] = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Price must be a number.");
                return true;
            }
        }

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
            else
                sender.sendMessage("/house " + action.name().toLowerCase() + " <house> - " + action.getDescription());

        }
    }
}
