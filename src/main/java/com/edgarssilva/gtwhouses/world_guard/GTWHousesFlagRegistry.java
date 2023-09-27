package com.edgarssilva.gtwhouses.world_guard;

import com.edgarssilva.gtwhouses.GTWHouses;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

public class GTWHousesFlagRegistry {


    public static final BooleanFlag HOUSE = new BooleanFlag("house");


    public static void registerFlags(GTWHouses plugin) {
        FlagRegistry flagRegistry = plugin.getWorldGuardPlugin().getFlagRegistry();
        flagRegistry.register(HOUSE);

    }
}
