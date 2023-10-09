package com.grandtheftwarzone.gtwhouses.events;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.dao.House;
import com.grandtheftwarzone.gtwhouses.dao.HouseBlock;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class HousePermissionEvents implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location location = block.getLocation();

        House house = GTWHouses.getHouseCache().getHouseInLocation(location);
        if (house == null) return; // If the block is not in a house, return

        boolean isOwner = player.getUniqueId().equals(house.getOwner());
        boolean isRenter = house.isRentable() && player.getUniqueId().equals(house.getRent().getRenter());

        // If the player is not the owner or renter, he can't break blocks
        if (!isOwner && !isRenter) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey! " + ChatColor.RESET + ChatColor.GRAY + "You don't have permissions to do that.");
            event.setCancelled(true);
            return;
        }

        World world = player.getServer().getWorld(house.getWorld());
        // Every house block is a block that can't be broken
        for (HouseBlock houseBlock : house.getBlocks()) {
            if (!houseBlock.getLocation(world).equals(location)) continue;

            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey! " + ChatColor.RESET + ChatColor.GRAY + "You can't break a house block.");
            event.setCancelled(true);
            return;
        }

        // If the player is the owner and the house is rented, he can't break blocks
        if (isOwner && house.isRentable() && house.getRent().isRented()) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey! " + ChatColor.RESET + ChatColor.GRAY + "You can't break blocks while your house is rented.");
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onUseBlocks(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return; // Only right click
        if (event.getHand() != EquipmentSlot.HAND) return; // Only main hand

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Location location = block.getLocation();

        House house = GTWHouses.getHouseCache().getHouseInLocation(location);
        if (house == null) return; // If the block is not in a house, return

        boolean isOwner = player.getUniqueId().equals(house.getOwner());
        boolean isRenter = house.isRentable() && player.getUniqueId().equals(house.getRent().getRenter());

        // If the player is not the owner or renter, he can't interact with blocks
        if (!isOwner && !isRenter) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey! " + ChatColor.RESET + ChatColor.GRAY + "You don't have permissions to do that.");
            event.setCancelled(true);
            return;
        }

        // If the player is the owner and the house is rented, he can't break blocks
        if (isOwner) {
            if (block.getType().toString().contains("DOOR")) return; // Allow doors

            if (house.isRentable() && house.getRent().isRented()) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey! " + ChatColor.RESET + ChatColor.GRAY + "You can't interact with blocks while your house is rented.");
                event.setCancelled(true);
            }
        }
    }
}
