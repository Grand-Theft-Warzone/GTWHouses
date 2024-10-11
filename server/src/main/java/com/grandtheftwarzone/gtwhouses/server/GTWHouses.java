package com.grandtheftwarzone.gtwhouses.server;

import com.grandtheftwarzone.gtwhouses.server.events.HouseEnterEvent;
import com.grandtheftwarzone.gtwhouses.server.events.HousePermissionEvents;
import com.grandtheftwarzone.gtwhouses.server.events.JoinEvent;
import com.grandtheftwarzone.gtwhouses.server.network.HousesPacketManager;
import com.grandtheftwarzone.gtwhouses.server.handlers.HouseCoordsHandler;
import com.grandtheftwarzone.gtwhouses.server.runnables.RentRunnable;
import com.grandtheftwarzone.gtwhouses.server.util.LoginMessageSystem;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class GTWHouses extends JavaPlugin {

    @Getter
    private static GTWHouses instance;

    @Getter
    private static HousesManager manager;
    @Getter
    private static HousesPacketManager packetManager;

    @Getter
    private static Economy economy;
    @Getter
    private static WorldEditPlugin worldEditPlugin;
    @Getter
    private static LoginMessageSystem loginMessageSystem = new LoginMessageSystem();

    @Getter
    private BukkitTask rentTask;

    public static final String houseSaveName = "houses.sav";

    @Override
    public void onEnable() {
        instance = this;

        if (!setupEconomy()) {
            getLogger().info("&cVault not found! Please install the Vault and reload the plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }

        if (!setupWorldEdit()) {
            getLogger().info("&cWorldEdit not found! Please install the WorldEdit and reload the plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }

        manager = new HousesManager();
        packetManager = new HousesPacketManager(this);
        loginMessageSystem.init();

        getCommand("house").setExecutor(new GTWHouseCommand());

        getServer().getPluginManager().registerEvents(new HousePermissionEvents(), this);
        getServer().getPluginManager().registerEvents(new HouseEnterEvent(), this);
        getServer().getPluginManager().registerEvents(new HouseCoordsHandler(), this);
        getServer().getPluginManager().registerEvents(loginMessageSystem, this);

        // Disabled for now, since ACsGuis loads the images for us
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
    }


    @Override
    public void onLoad() {
        manager.load();

        //TODO: Check if this can be done asynchronously
        if (rentTask != null) rentTask.cancel();
        rentTask = Bukkit.getScheduler().runTaskTimer(this, new RentRunnable(), 20 * 10, 20 * 60 * 30); //30 minutes
    }

    @Override
    public void onDisable() {
        manager.saveSync();
        getLogger().info("&GTW Houses has been disabled!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().info("&cEconomy not found! Please install an economy plugin and reload the plugin.");
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    private boolean setupWorldEdit() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");

        if (!(plugin instanceof WorldEditPlugin)) return false;

        worldEditPlugin = (WorldEditPlugin) plugin;

        return true;
    }
}