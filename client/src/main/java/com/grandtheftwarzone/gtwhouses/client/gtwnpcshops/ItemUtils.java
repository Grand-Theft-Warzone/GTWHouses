package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

public class ItemUtils {
    public static String serializeItemStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return "";
        }
        NBTTagCompound nbt = new NBTTagCompound();
        stack.writeToNBT(nbt); // Write the ItemStack's data to NBT
        return nbt.toString(); // Convert the NBT to a string
    }

    // Deserialize an ItemStack from a string
    public static ItemStack deserializeItemStack(String data) {
        if (data.isEmpty()) {
            return ItemStack.EMPTY;
        }
        try {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.merge(JsonToNBT.getTagFromJson(data)); // Parse the NBT from the string
            return new ItemStack(nbt); // Create an ItemStack from the NBT
        } catch (Exception e) {
            e.printStackTrace();
            return ItemStack.EMPTY;
        }
    }
}
