package com.grandtheftwarzone.gtwhouses.util;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import lombok.Getter;
import me.phoenixra.atum.core.database.Database;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class LoginMessageSystem implements Listener {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public void sendOrStore(OfflinePlayer player, String message) {
        if (player.isOnline()) {
            player.getPlayer().sendMessage(message);
        } else {
            storeMessage(player.getUniqueId(), message);
        }
    }

    public void storeMessage(UUID uuid, String message) {
        Database db = GTWHouses.getHouseDatabase().getDatabase();
        try {
            PreparedStatement statement = db.getConnection().prepareStatement("INSERT INTO messages (uuid, message, time) VALUES (?, ?, ?)");
            statement.setString(1, uuid.toString());
            statement.setString(2, message);
            statement.setLong(3, System.currentTimeMillis());
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Database db = GTWHouses.getHouseDatabase().getDatabase();

        Player player = event.getPlayer();
        if (player == null) return;

        try {
            PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM messages WHERE uuid = ?");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet rs = statement.executeQuery();

            ArrayList<Message> messages = new ArrayList<>();
            while (rs.next()) messages.add(new Message(rs.getString("message"), new Date(rs.getLong("time"))));
            if (messages.isEmpty()) return;

            player.sendMessage("You have " + messages.size() + " unread messages:");
            for (Message message : messages) {
                player.sendMessage(ChatColor.YELLOW + dateFormat.format(message.getDate()) + ChatColor.RESET + " - " + message.getMessage());
            }

            PreparedStatement deleteStatement = db.getConnection().prepareStatement("DELETE FROM messages WHERE uuid = ?");
            deleteStatement.setString(1, player.getUniqueId().toString());
            deleteStatement.executeUpdate();

            statement.close();
            deleteStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
