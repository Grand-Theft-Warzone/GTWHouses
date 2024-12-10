package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ShopTooltipHandler {
    @SubscribeEvent
    public static void onTooltip(net.minecraftforge.event.entity.player.ItemTooltipEvent event) {
        if (event.getItemStack().hasTagCompound() && event.getItemStack().getTagCompound().hasKey("cost")) {
            int cost = event.getItemStack().getTagCompound().getInteger("cost");
            event.getToolTip().add("Cost: " + cost + "$");
        }

        if (event.getItemStack().hasTagCompound() && event.getItemStack().getTagCompound().hasKey("level")) {
            int level = event.getItemStack().getTagCompound().getInteger("level");
            event.getToolTip().add("Level: " + level);
        }
    }
}
