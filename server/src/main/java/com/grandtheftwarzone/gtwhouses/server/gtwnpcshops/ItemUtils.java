package com.grandtheftwarzone.gtwhouses.server.gtwnpcshops;

import net.minecraft.server.v1_12_R1.MojangsonParser;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;

public class ItemUtils {
    public static String serializeItemStack(org.bukkit.inventory.ItemStack item) {
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        if (nmsStack == null) {
            return "";
        }
        return nmsStack.save(new NBTTagCompound()).toString();
    }

    public static org.bukkit.inventory.ItemStack deserializeItemStack(String data) {
        try {
            if (data.isEmpty()) {
                return null;
            }
            NBTTagCompound nbt = MojangsonParser.parse(data);
            net.minecraft.server.v1_12_R1.ItemStack nmsStack = new net.minecraft.server.v1_12_R1.ItemStack(nbt);
            return CraftItemStack.asBukkitCopy(nmsStack);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
