package com.edgarssilva.gtwhouses;

import com.edgarssilva.gtwhouses.commands.HouseCommand;
import com.edgarssilva.gtwhouses.events.HouseBlockBreak;
import com.edgarssilva.gtwhouses.manager.HouseManager;
import com.edgarssilva.gtwhouses.runnables.RentRunnable;
import com.edgarssilva.gtwhouses.world_guard.GTWHousesFlagRegistry;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.phoenixra.atum.core.AtumPlugin;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.utils.StringUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.logging.Logger;

public final class GTWHouses extends AtumPlugin {

    private static GTWHouses instance;
    private static Economy economy;
    private static WorldGuardPlugin worldGuardPlugin;
    private static WorldEditPlugin worldEditPlugin;
    private final Logger logger = this.getLogger();

    public static final String CONFIG_FOLDER = "plugins/GTWHouses";

    @Override
    protected void handleEnable() {
        instance = this;

        if (!setupEconomy()) {
            logger.info(StringUtils.format("&cVault not found! Please install the Vault and reload the plugin."));
            getServer().getPluginManager().disablePlugin(this);
        }

        if (!setupWorldGuard()) {
            logger.info(StringUtils.format("&cWorldGuard not found! Please install the WorldGuard and reload the plugin."));
            getServer().getPluginManager().disablePlugin(this);
        }

        if (!setupWorldEdit()) {
            logger.info(StringUtils.format("&cWorldEdit not found! Please install the WorldEdit and reload the plugin."));
            getServer().getPluginManager().disablePlugin(this);
        }

        HouseManager.load();
    }

    @Override
    protected void handleLoad() {
        super.handleLoad();

        GTWHousesFlagRegistry.registerFlags(this);
    }

    @Override
    protected void handleReload() {
        super.handleReload();

        //TODO: Check if this can be done asynchronously
        Bukkit.getScheduler().runTaskTimer(this, new RentRunnable(), 20 * 10, 20 * 60);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, HouseManager.SAVE_RUNNABLE, 20 * 10, 20 * 20);
    }

    @Override
    protected void handleDisable() {
        HouseManager.save();

        logger.info(StringUtils.format("&GTW Houses has been disabled!"));
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
                new HouseBlockBreak()
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
            logger.info(StringUtils.format("&cEconomy not found! Please install an economy plugin and reload the plugin."));
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    private boolean setupWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        if (!(plugin instanceof WorldGuardPlugin)) return false;

        worldGuardPlugin = (WorldGuardPlugin) plugin;

        return true;
    }

    private boolean setupWorldEdit() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");

        if (!(plugin instanceof WorldEditPlugin)) return false;

        worldEditPlugin = (WorldEditPlugin) plugin;

        return true;
    }

    public Economy getEconomy() {
        return economy;
    }

    public WorldGuardPlugin getWorldGuardPlugin() {
        return worldGuardPlugin;
    }

    public WorldEditPlugin getWorldEditPlugin() {
        return worldEditPlugin;
    }

    public static GTWHouses getInstance() {
        return instance;
    }
}
