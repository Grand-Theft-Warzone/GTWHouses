package com.grandtheftwarzone.gtwhouses.server.gtwnpcshops.events;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.Shop;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.SellShopGUIOpenPacket;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.ShopGUIOpenPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import org.bukkit.entity.Pig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class ShopEvents implements Listener {

    @EventHandler
    public void onPigClick(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Pig) {
            GTWHouses.getPacketManager().sendPacket(
                    event.getPlayer(),
                    new SellShopGUIOpenPacket(GTWHouses.getShopsManager().getItems())
            );
        }
    }

}
