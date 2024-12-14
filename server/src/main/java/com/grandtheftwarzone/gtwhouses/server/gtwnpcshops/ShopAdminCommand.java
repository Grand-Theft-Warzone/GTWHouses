package com.grandtheftwarzone.gtwhouses.server.gtwnpcshops;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.AdminShopGUI;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.OpenAdminShopGuiPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopAdminCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        if (!sender.hasPermission("gtwshops.admin") || !sender.isOp()) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        GTWHouses.getPacketManager().sendPacket(((Player) sender), new OpenAdminShopGuiPacket(AdminShopGUI.SHOP_LIST, GTWHouses.getShopsManager().getShops(), GTWHouses.getShopsManager().getItems()));
        return true;
    }
}
