package com.grandtheftwarzone.gtwhouses.server;

import com.google.common.collect.Lists;
import com.grandtheftwarzone.gtwhouses.server.commands.HouseCommand;
import com.grandtheftwarzone.gtwhouses.server.events.HouseEnterEvent;
import com.grandtheftwarzone.gtwhouses.server.events.HousePermissionEvents;
import com.grandtheftwarzone.gtwhouses.server.network.GTWHousesPacketManager;
import com.grandtheftwarzone.gtwhouses.server.network.TinyProtocol.TinyProtocol;
import com.grandtheftwarzone.gtwhouses.server.network.handlers.HouseCoordsHandler;
import com.grandtheftwarzone.gtwhouses.server.runnables.RentRunnable;
import com.grandtheftwarzone.gtwhouses.server.util.LoginMessageSystem;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import me.phoenixra.atum.core.AtumPlugin;
import me.phoenixra.atum.core.command.AtumCommand;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.List;


public class GTWHouses extends AtumPlugin {

    @Getter
    private static GTWHouses instance;
    @Getter
    private static HousesManager manager;
    @Getter
    private static Economy economy;
    @Getter
    private static WorldEditPlugin worldEditPlugin;
    @Getter
    private static GTWHousesPacketManager packetManager;
    @Getter
    private static LoginMessageSystem loginMessageSystem = new LoginMessageSystem();

    @Override
    protected void handleEnable() {
        instance = this;

        if (!setupEconomy()) {
            getLogger().info("&cVault not found! Please install the Vault and reload the plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }

        if (!setupWorldEdit()) {
            getLogger().info("&cWorldEdit not found! Please install the WorldEdit and reload the plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }

        // ConfigurationSerialization.registerClass(House.class, "House");
        //ConfigurationSerialization.registerClass(HouseBlock.class, "HouseBlock");

        manager = new HousesManager();
        packetManager = new GTWHousesPacketManager(this);
        loginMessageSystem.init();
    }

    @Override
    protected void handleReload() {
        super.handleReload();
        manager.load();

        //TODO: Check if this can be done asynchronously
        this.getScheduler().runTimer(20 * 10, 20 * 60 * 30, new RentRunnable()); //30 minutes
    }

    @Override
    protected void handleDisable() {
        manager.saveSync();
        getLogger().info("&GTW Houses has been disabled!");
    }

    @Override
    protected List<AtumCommand> loadPluginCommands() {
        return Lists.newArrayList(
                new HouseCommand(this)
        );
    }

    @Override
    protected List<Listener> loadListeners() {
        return Lists.newArrayList(
                new HousePermissionEvents(),
                new HouseEnterEvent(),
                new HouseCoordsHandler(),
                loginMessageSystem
        );
    }

    @Override
    protected int getAtumAPIVersion() {
        return 12;
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