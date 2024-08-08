package com.grandtheftwarzone.gtwhouses.server.util;

import com.grandtheftwarzone.gtwhouses.common.GTWHousesUtils;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class LoginMessageSystem implements Listener {

    private final HashMap<UUID, ArrayList<Message>> messages = new HashMap<>();

    private FileConfiguration messageConfig;

    public void init() {
        File messageFile = new File(GTWHouses.getInstance().getDataFolder(), "messages.yml");
        if (!messageFile.exists()) GTWHouses.getInstance().saveResource("messages.yml", false);

        messageConfig = YamlConfiguration.loadConfiguration(messageFile);
        loadMessages();
    }

    private void loadMessages() {
        for (String key : messageConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            ArrayList<Message> messages = new ArrayList<>();

            messageConfig.getLongList(key).forEach(time -> {
                String message = messageConfig.getString(key + "." + time);
                messages.add(new Message(message, new Date(time)));
            });

            this.messages.put(uuid, messages);
        }

    }

    private BukkitTask runningTask;

    private void saveSync() {
        GTWHouses.getInstance().saveResource("messages.yml", true);

        for (UUID uuid : messages.keySet())
            for (Message message : messages.get(uuid))
                messageConfig.set(uuid.toString() + "." + message.getDate().getTime(), message.getMessage());
    }

    private void save() {
        if (runningTask != null) runningTask.cancel();
        runningTask = new BukkitRunnable() {
            @Override
            public void run() {
                saveSync();
            }
        }.runTaskLaterAsynchronously(GTWHouses.getInstance(), 20 * 2);
    }

    public void sendOrStore(OfflinePlayer player, String message) {
        if (player.isOnline()) {
            player.getPlayer().sendMessage(message);
        } else {
            storeMessage(player.getUniqueId(), message);
        }
    }

    public void storeMessage(UUID uuid, String message) {
        messages.computeIfAbsent(uuid, k -> new ArrayList<>()).add(new Message(message, new Date()));
        save();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        ArrayList<Message> playerMessages = messages.get(player.getUniqueId());
        if (playerMessages == null || playerMessages.isEmpty()) return;

        player.sendMessage("You have " + playerMessages.size() + " unread messages:");
        for (Message message : playerMessages)
            player.sendMessage(ChatColor.YELLOW + GTWHousesUtils.DATE_FORMAT.format(message.getDate()) + ChatColor.RESET + " - " + message.getMessage());

        messages.remove(player.getUniqueId());
        save();
    }

    @Getter
    private static class Message {
        private final String message;
        private final Date date;

        public Message(String message, Date date) {
            this.message = message;
            this.date = date;
        }
    }
}
