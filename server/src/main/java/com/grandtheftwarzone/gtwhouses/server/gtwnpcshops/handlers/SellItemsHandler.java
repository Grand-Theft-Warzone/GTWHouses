package com.grandtheftwarzone.gtwhouses.server.gtwnpcshops.handlers;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItem;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.SellItemsPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.network.GTWPacketHandler;
import net.minecraft.server.v1_12_R1.Item;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class SellItemsHandler implements GTWPacketHandler.PacketHandler<SellItemsPacket> {
    @Override
    public void handle(Player sender, SellItemsPacket packet) {
        int profit = 0;

        int i = 0;
        for (Map.Entry<String, Integer> entry : packet.getItems().entrySet()) {
            ShopItem item = GTWHouses.getShopsManager().getItem(entry.getKey());
            if (item == null) continue;

            profit += item.getSellPrice() * entry.getValue();
        }

        //Remove the items from the player inventory that support modded items
        for (Map.Entry<String, Integer> entry : packet.getItems().entrySet()) {
            ItemStack itemStack = CraftItemStack.asBukkitCopy(new net.minecraft.server.v1_12_R1.ItemStack(
                    net.minecraft.server.v1_12_R1.Item.b(entry.getKey()),  entry.getValue()));
            sender.getInventory().removeItem(itemStack);
        }

        GTWHouses.getEconomy().depositPlayer(sender.getPlayer(), profit);
        sender.sendMessage("You sold the items for " + ChatColor.GREEN + "$" + profit + ChatColor.RESET + ".");
    }
}
