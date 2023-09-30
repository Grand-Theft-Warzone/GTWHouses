package com.edgarssilva.gtwhouses.world_guard;

import com.edgarssilva.gtwhouses.GTWHouses;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

public class GTWHousesFlagRegistry {


    public static final BooleanFlag HOUSE = new BooleanFlag("house");


    public static void registerFlags(WorldGuardPlugin plugin) {
        plugin.getFlagRegistry().register(HOUSE);

    }
}
