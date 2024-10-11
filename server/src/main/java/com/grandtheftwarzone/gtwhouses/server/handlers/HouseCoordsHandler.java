package com.grandtheftwarzone.gtwhouses.server.handlers;

import com.grandtheftwarzone.gtwhouses.common.data.HouseBlock;
import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.HouseCoordsC2SPacket;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.HouseCoordsS2CPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.network.GTWPacketHandler;
import com.grandtheftwarzone.gtwhouses.server.util.HouseUtils;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HouseCoordsHandler implements GTWPacketHandler.PacketHandler<HouseCoordsC2SPacket>, Listener {
    private static final HashMap<UUID, HouseCoordsC2SPacket> pendingPackets = new HashMap<>();
    public static final HashMap<UUID, List<HouseBlock>> houseBlocks = new HashMap<>();

    @Override
    public void handle(Player sender, HouseCoordsC2SPacket packet) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "How did you get this packet? You are not allowed to do this!");
            return;
        }

        ItemStack item = new ItemStack(Material.WOOD_AXE, 1);
        item.getItemMeta().setDisplayName(ChatColor.RED + "House Area Selector");

        if (sender.getInventory().contains(item.getType()))
            sender.getInventory().removeItem(item);

        sender.getInventory().addItem(item);

        clearWorldEditSelection(sender);

        pendingPackets.put(sender.getUniqueId(), packet);
        sender.sendMessage(ChatColor.GOLD + "Select the house area with the " + ChatColor.RED + "wood axe " + ChatColor.GOLD + "!");
    }

    @EventHandler(ignoreCancelled = false)
    public void onAreaSelection(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType() != Material.WOOD_AXE) return;
        if (!pendingPackets.containsKey(event.getPlayer().getUniqueId())) return;

        Selection selection = GTWHouses.getWorldEditPlugin().getSelection(event.getPlayer());
        if (selection == null) return;

        int minPosX = selection.getMinimumPoint().getBlockX();
        int minPosY = selection.getMinimumPoint().getBlockY();
        int minPosZ = selection.getMinimumPoint().getBlockZ();

        int maxPosX = selection.getMaximumPoint().getBlockX();
        int maxPosY = selection.getMaximumPoint().getBlockY();
        int maxPosZ = selection.getMaximumPoint().getBlockZ();

        HouseCoordsC2SPacket packet = pendingPackets.remove(event.getPlayer().getUniqueId());
        houseBlocks.put(event.getPlayer().getUniqueId(), HouseUtils.getHouseBlocks(minPosX, minPosY, minPosZ, maxPosX, maxPosY, maxPosZ, selection.getWorld()));

        GTWHouses.getPacketManager().sendPacket(event.getPlayer(), new HouseCoordsS2CPacket(
                packet.getName(), packet.getBuyCost(), packet.getRentCost(), packet.getType(),
                minPosX, minPosY, minPosZ, maxPosX, maxPosY, maxPosZ, selection.getWorld().getName(),
                packet.getImageURL(),
                houseBlocks.get(event.getPlayer().getUniqueId()).size(), packet.getOriginalName()
                //HouseUtils.getHouseBlocks(minPosX, minPosY, minPosZ, maxPosX, maxPosY, maxPosZ, selection.getWorld())
        ));

        event.getPlayer().getInventory().removeItem(new ItemStack(Material.WOOD_AXE, 1));
    }

    public void clearWorldEditSelection(Player player) {
        LocalSession session = GTWHouses.getWorldEditPlugin().getSession(player);
        if (session == null) return;

        session.getRegionSelector(BukkitUtil.getLocalWorld(player.getWorld())).clear();
        session.clearHistory();
    }
}
