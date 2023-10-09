package com.grandtheftwarzone.gtwhouses;

import com.grandtheftwarzone.gtwhouses.commands.HouseCommand;
import com.grandtheftwarzone.gtwhouses.database.HouseCache;
import com.grandtheftwarzone.gtwhouses.database.HouseDatabase;
import com.grandtheftwarzone.gtwhouses.events.HouseEnterEvent;
import com.grandtheftwarzone.gtwhouses.events.HousePermissionEvents;
import com.grandtheftwarzone.gtwhouses.events.PlayerJoinListener;
import com.grandtheftwarzone.gtwhouses.runnables.RentRunnable;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import me.phoenixra.atum.core.AtumPlugin;
import me.phoenixra.atum.core.command.AtumCommand;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.List;

public final class GTWHouses extends AtumPlugin {

    @Getter private static GTWHouses instance;
    @Getter private static Economy economy;
    @Getter private static WorldEditPlugin worldEditPlugin;
    @Getter private static HouseDatabase houseDatabase;
    @Getter private static HouseCache houseCache;

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

        houseDatabase = new HouseDatabase(this);
        houseCache = new HouseCache();
    }

    @Override
    protected void handleReload() {
        super.handleReload();

        //TODO: Check if this can be done asynchronously
        this.getScheduler().runTimer(20 * 10, 20 * 60 * 30, new RentRunnable()); //30 minutes
    }

    @Override
    protected void handleDisable() {
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
                new PlayerJoinListener(),
                new HouseEnterEvent()
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
