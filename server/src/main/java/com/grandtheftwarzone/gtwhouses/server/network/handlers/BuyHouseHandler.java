package com.grandtheftwarzone.gtwhouses.server.network.handlers;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.BuyHouseC2SPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.network.GTWPacketHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.GREEN;

public class BuyHouseHandler implements GTWPacketHandler.PacketHandler<BuyHouseC2SPacket> {
    @Override
    public void handle(Player sender, BuyHouseC2SPacket packet) {
        House house = GTWHouses.getManager().getHouse(packet.getHouseName());
        if (house == null) return;
        if (!house.isForSale()) sender.sendMessage(ChatColor.RED + "House is not for sale");

        double cost = house.isOwned() ? house.getSellCost() : house.getBuyCost();
        if (!GTWHouses.getEconomy().has(sender, cost)) {
            sender.sendMessage(ChatColor.RED + "You don't have $" + cost + " to buy this house");
            return;
        }

        if (house.isOwned()) {
            if (house.getOwner().equals(sender.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You already own this house");
                return;
            }

            Player oldOwner = GTWHouses.getInstance().getServer().getPlayer(house.getOwner());
            if (oldOwner != null) {
                GTWHouses.getEconomy().depositPlayer(oldOwner, cost);

                if (oldOwner.isOnline())
                    oldOwner.getPlayer().sendMessage(GREEN + "Your house " + house.getName() + " has been sold to " + sender.getName() + " for " + GREEN + "$" + cost);
                else
                    GTWHouses.getLoginMessageSystem().storeMessage(oldOwner.getUniqueId(), "Your house " + house.getName() + " has been sold to " + sender.getName() + " for " + GREEN + "$" + cost);
            }
        }

        GTWHouses.getEconomy().withdrawPlayer(sender, cost);
        house.setOwner(sender.getUniqueId());
        house.resetRent();
        GTWHouses.getManager().save();

        sender.sendMessage(GREEN + "You bought the house " + house.getName() + " for " + GREEN + "$" + cost);
    }
}
