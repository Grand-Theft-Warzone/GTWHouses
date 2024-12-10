package com.grandtheftwarzone.gtwhouses.server;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.Shop;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.ShopGUIOpenPacket;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.GTWHouseCommand;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.HousesManager;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.events.BlockInteractEvent;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.events.HouseEnterEvent;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.events.HousePermissionEvents;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.events.JoinEvent;
import com.grandtheftwarzone.gtwhouses.server.gtwnpcshops.ShopAdminCommand;
import com.grandtheftwarzone.gtwhouses.server.gtwnpcshops.ShopsManager;
import com.grandtheftwarzone.gtwhouses.server.gtwnpcshops.events.ShopEvents;
import com.grandtheftwarzone.gtwhouses.server.network.GTWPacketManager;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.handlers.HouseCoordsHandler;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.runnables.RentRunnable;
import com.grandtheftwarzone.gtwhouses.server.gtwhouses.util.LoginMessageSystem;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class GTWHouses extends JavaPlugin {

    @Getter
    private static GTWHouses instance;

    @Getter
    private static HousesManager housesManager;
    @Getter
    private static ShopsManager shopsManager;
    @Getter
    private static GTWPacketManager packetManager;

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

        housesManager = new HousesManager();
        shopsManager = new ShopsManager();

        packetManager = new GTWPacketManager(this);
        loginMessageSystem.init();

        getCommand("house").setExecutor(new GTWHouseCommand());
        getCommand("shop").setExecutor(new ShopAdminCommand());

        getServer().getPluginManager().registerEvents(new HousePermissionEvents(), this);
        getServer().getPluginManager().registerEvents(new HouseEnterEvent(), this);
        getServer().getPluginManager().registerEvents(new HouseCoordsHandler(), this);
        getServer().getPluginManager().registerEvents(loginMessageSystem, this);
        getServer().getPluginManager().registerEvents(new BlockInteractEvent(), this);
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);


        getServer().getPluginManager().registerEvents(new ShopEvents(), this);

        housesManager.load();
    }


    @Override
    public void onLoad() {

        //TODO: Check if this can be done asynchronously
        if (rentTask != null) rentTask.cancel();
        rentTask = Bukkit.getScheduler().runTaskTimer(this, new RentRunnable(), 20 * 10, 20 * 60 * 30); //30 minutes
    }

    @Override
    public void onDisable() {
        housesManager.saveSync();
        shopsManager.saveSync();
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

    //Used by a custom version of znpcs using reflection
    public void openShop(Player player, String shopname) {
        Shop shop = getShopsManager().getShop(shopname);
        if (shop == null) {
            getLogger().warning("Shop " + shopname + " not found!");
            return;
        }

        getPacketManager().sendPacket(player, new ShopGUIOpenPacket(shop, getShopsManager().getShopItems(shopname)));
    }
}