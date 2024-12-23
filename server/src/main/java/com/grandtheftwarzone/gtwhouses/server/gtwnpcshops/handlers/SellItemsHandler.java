package com.grandtheftwarzone.gtwhouses.server.gtwnpcshops.handlers;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItem;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItemAmount;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.SellItemsPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.gtwnpcshops.ItemUtils;
import com.grandtheftwarzone.gtwhouses.server.network.GTWPacketHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellItemsHandler implements GTWPacketHandler.PacketHandler<SellItemsPacket> {
    @Override
    public void handle(Player sender, SellItemsPacket packet) {
        int profit = 0;

        int i = 0;
        ItemStack[] items = new ItemStack[packet.getItems().size()];

        for (ShopItemAmount itemAmount : packet.getItems()) {
            ShopItem item = GTWHouses.getShopsManager().getItem(itemAmount.getItemStackSerialized());
            if (item == null) continue;

            ItemStack stack = ItemUtils.deserializeItemStack(itemAmount.getItemStackSerialized());
            if (stack == null) continue;
            stack.setAmount(itemAmount.getAmount());
            items[i++] = stack;

            profit += item.getSellPrice() * itemAmount.getAmount();
        }

        sender.getInventory().removeItem(items);

        GTWHouses.getEconomy().depositPlayer(sender.getPlayer(), profit);
        sender.sendMessage("You sold the items for " + ChatColor.GREEN + "$" + profit + ChatColor.RESET + ".");
    }
}
