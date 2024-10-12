package com.grandtheftwarzone.gtwhouses.server.events;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.data.HousePlacedBlock;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.actions.EditBlocksAction;
import com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.UseBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Map;
import java.util.Objects;

public class HousePermissionEvents implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onBlockBreak(BreakBlockEvent worldGuardEvent) {
        if (!(worldGuardEvent.getOriginalEvent() instanceof BlockBreakEvent))
            return; // If the event is not a block break event, return

        BlockBreakEvent event = (BlockBreakEvent) worldGuardEvent.getOriginalEvent();
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location location = block.getLocation();

        House house = GTWHouses.getManager().getHouseInLocation(location);
        if (house == null) return; // If the block is not in a house, return

        //If player is editing house blocks, allow block  breaking
        if (Objects.equals(house, EditBlocksAction.editing.get(player.getUniqueId()))) {
            worldGuardEvent.setResult(PlaceBlockEvent.Result.ALLOW);
            return;
        }

        boolean isOwner = player.getUniqueId().equals(house.getOwner());
        boolean isRenter = player.getUniqueId().equals(house.getRenter());

        // If the player is not the owner nor the renter, he can't break blocks
        if (!isOwner && !isRenter) {
//            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey! " + ChatColor.RESET + ChatColor.GRAY + "You don't have permissions to do that.");
            worldGuardEvent.setResult(BreakBlockEvent.Result.DENY);
            return;
        }

        // If the player is the owner and the house is rented, he can't break blocks
        if (isOwner && house.isRented()) {
//            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey! " + ChatColor.RESET + ChatColor.GRAY + "You can't break blocks while your house is rented.");
            worldGuardEvent.setResult(BreakBlockEvent.Result.DENY);
            return;
        }

        Map<Integer, HousePlacedBlock> placedBlocks = GTWHouses.getManager().getPlacedBlocks(house.getName());
        if (placedBlocks == null) return;

        Integer hash = HousePlacedBlock.getHash(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (!placedBlocks.containsKey(hash)) {
            event.setCancelled(true);
            return;
        }

        placedBlocks.remove(hash); // Remove the block from the placed blocks since it was broken
        Bukkit.getScheduler().runTask(GTWHouses.getInstance(), () -> block.setType(org.bukkit.Material.AIR));
        System.out.println("Broke: " + hash + " to " + house.getName() + " at " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());

        worldGuardEvent.setResult(BreakBlockEvent.Result.ALLOW);

        // Renter can break non house blocks
//        event.getBlock().getDrops().forEach(drop -> event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drop));
//        event.getBlock().setType(org.bukkit.Material.AIR);
//        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onUseBlocks(UseBlockEvent worldGuardEvent) {
        if (!(worldGuardEvent.getOriginalEvent() instanceof PlayerInteractEvent))
            return; // If the event is not a player interact event, return

        PlayerInteractEvent event = (PlayerInteractEvent) worldGuardEvent.getOriginalEvent();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return; // Only right click
        if (event.getHand() != EquipmentSlot.HAND) return; // Only main hand

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Location location = block.getLocation();

        House house = GTWHouses.getManager().getHouseInLocation(location);
        if (house == null) return; // If the block is not in a house, return

        //If player is editing house blocks, allow block usage
        if (Objects.equals(house, EditBlocksAction.editing.get(player.getUniqueId()))) {
            worldGuardEvent.setResult(PlaceBlockEvent.Result.ALLOW);
            return;
        }

        boolean isOwner = player.getUniqueId().equals(house.getOwner());
        boolean isRenter = player.getUniqueId().equals(house.getRenter());

        // If the player is not the owner or the renter, he can't interact with blocks
        if (!isOwner && !isRenter) {
//            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey! " + ChatColor.RESET + ChatColor.GRAY + "You don't have permissions to do that.");
            //event.setCancelled(true);
            worldGuardEvent.setResult(UseBlockEvent.Result.DENY);
            return;
        }

        // If the player is the owner and the house is rented, he can't use blocks
        if (block.getType().toString().contains("DOOR")) {
            worldGuardEvent.setResult(UseBlockEvent.Result.ALLOW);
            return; // Allow doors
        }

        // If the player is the owner and the house is rented, he can't use blocks except doors
        if (isOwner && house.isRented()) {
//            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey! " + ChatColor.RESET + ChatColor.GRAY + "You can't interact with blocks while your house is rented.");
            // event.setCancelled(true);
            worldGuardEvent.setResult(UseBlockEvent.Result.DENY);
        }

        worldGuardEvent.setResult(UseBlockEvent.Result.ALLOW);
//        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlaceBlock(PlaceBlockEvent worldGuardEvent) {
        if (!(worldGuardEvent.getOriginalEvent() instanceof BlockPlaceEvent))
            return; // If the event is not a block place event, return

        BlockPlaceEvent event = (BlockPlaceEvent) worldGuardEvent.getOriginalEvent();
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        Location location = block.getLocation();

        House house = GTWHouses.getManager().getHouseInLocation(location);
        if (house == null) return; // If the block is not in a house, return

        //If player is editing house blocks, allow block placement
        if (Objects.equals(house, EditBlocksAction.editing.get(player.getUniqueId()))) {
            worldGuardEvent.setResult(PlaceBlockEvent.Result.ALLOW);
            return;
        }

        boolean isOwner = player.getUniqueId().equals(house.getOwner());
        boolean isRenter = player.getUniqueId().equals(house.getRenter());

        // If the player is not the owner or the renter, he can't interact with blocks
        if (!isOwner && !isRenter) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey! " + ChatColor.RESET + ChatColor.GRAY + "You don't have permissions to do that.");
            worldGuardEvent.setResult(PlaceBlockEvent.Result.DENY);
            return;
        }

        // If the player is the owner and the house is rented, he can't use blocks except doors
        if (isOwner && house.isRented()) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey! " + ChatColor.RESET + ChatColor.GRAY + "You can't place blocks while your house is rented.");
            worldGuardEvent.setResult(PlaceBlockEvent.Result.DENY);
            return;
        }

        Map<Integer, HousePlacedBlock> placedBlocks = GTWHouses.getManager().getPlacedBlocks(house.getName());
        if (placedBlocks == null) return;

        Integer hash = HousePlacedBlock.getHash(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        placedBlocks.put(hash, new HousePlacedBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ()));

        System.out.println("Added: " + hash + " to " + house.getName() + " at " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());

        worldGuardEvent.setResult(PlaceBlockEvent.Result.ALLOW);
        //  event.setCancelled(false);
    }
}
