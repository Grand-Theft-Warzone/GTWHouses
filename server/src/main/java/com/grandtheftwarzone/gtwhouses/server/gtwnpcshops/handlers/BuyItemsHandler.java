package com.grandtheftwarzone.gtwhouses.server.gtwnpcshops.handlers;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItem;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItemAmount;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.BuyItemsPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.gtwnpcshops.ItemUtils;
import com.grandtheftwarzone.gtwhouses.server.network.GTWPacketHandler;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class BuyItemsHandler implements GTWPacketHandler.PacketHandler<BuyItemsPacket> {
    @Override
    public void handle(Player sender, BuyItemsPacket packet) {
        int cost = 0;
        int levelRequirement = 0;

        int i = 0;
        ItemStack[] items = new ItemStack[packet.getItems().size()];

        for (ShopItemAmount itemAmount : packet.getItems()) {
            ShopItem item = GTWHouses.getShopsManager().getItem(itemAmount.getItemStackSerialized());
            if (item == null) continue;

            ItemStack stack = ItemUtils.deserializeItemStack(itemAmount.getItemStackSerialized());
            if (stack == null) continue;
            stack.setAmount(itemAmount.getAmount());
            items[i++] = stack;

            cost += item.getBuyPrice() * itemAmount.getAmount();
            levelRequirement = Math.max(item.getBuyLevel(), levelRequirement);
        }

        //TODO: Change this into a gtw level plugin
        if (sender.getLevel() < levelRequirement) {
            sender.sendMessage("You need to be level " + levelRequirement + " to buy these items.");
            return;
        }

        if (!GTWHouses.getEconomy().has(sender.getPlayer(), cost)) {
            sender.sendMessage(ChatColor.RED + "You don't have enough money to buy these items.");
            return;
        }

        Map<Integer, ItemStack> leftOver = sender.getInventory().addItem(items);
        if (!leftOver.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "You don't have enough space in your inventory to buy these items.");
            sender.sendMessage(ChatColor.BOLD + "Pickup them up from the ground.");
        }

        for (ItemStack item : leftOver.values()) {
            sender.getWorld().dropItem(sender.getLocation(), item);
        }

        GTWHouses.getEconomy().withdrawPlayer(sender.getPlayer(), cost);
        sender.sendMessage("You bought the items for " + ChatColor.GREEN + "$" + cost + ChatColor.RESET + ".");
    }
}
