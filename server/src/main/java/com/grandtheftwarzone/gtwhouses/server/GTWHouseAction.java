package com.grandtheftwarzone.gtwhouses.server;

import com.grandtheftwarzone.gtwhouses.common.HouseActions;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.OpenGUIC2SPacket;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.HousesGUIS2CPacket;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.RegisterHouseS2CPacket;
import com.grandtheftwarzone.gtwhouses.server.GTWHouses;
import com.grandtheftwarzone.gtwhouses.server.actions.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GTWHouseAction {
    public static void handle(HouseActions action, Player player, String houseName, int... args) throws InvalidActionException {
        if (action == null) return;

        if (!player.isOp() && action.isRequiresAdmin())
            throw new InvalidActionException(ChatColor.RED + "You do not have permission to perform this action");

        House house = null;
        if (action.isRequiresHouse()) {
            house = GTWHouses.getManager().getHouse(houseName);
            if (house == null) throw new InvalidActionException(ChatColor.RED + "House not found");
        }

        switch (action) {
            case GUI:
                GTWHouses.getPacketManager().sendPacket(player, new HousesGUIS2CPacket(OpenGUIC2SPacket.OpenGUIType.HOUSES, GTWHouses.getManager().getHouses()));
                break;
            case Buy:
                BuyAction.handle(house, player);
                break;
            case Rent:
                RentAction.handle(house, player);
                break;
            case Unrent:
                UnrentAction.handle(house, player);
                break;
            case Sell:
                //TODO: Validate args
                SellAction.handle(house, player, args[0]);
                break;
            case CitySell:
                CitySellAction.handle(house, player);
                break;
            case Unsell:
                UnsellAction.handle(house, player);
                break;
            case Rentable:
                RentableAction.handle(house, player);
                break;
            case Unrentable:
                UnrentableAction.handle(house, player);
                break;


            case List:
                ListAction.handle(player);
                break;
            case Top:
                TopAction.handle(player);
                break;

            //Admin
            case Admin:
                GTWHouses.getPacketManager().sendPacket(player, new HousesGUIS2CPacket(OpenGUIC2SPacket.OpenGUIType.ADMIN_PANEL, GTWHouses.getManager().getHouses()));
                break;
            case Register:
                GTWHouses.getPacketManager().sendPacket(player, new RegisterHouseS2CPacket());
                break;
            case Remove:
                RemoveAction.handle(house, player);
                break;
            case Reset:
                ResetAction.handle(house, player);
                break;
            case Unregister:
                UnregisterAction.handle(house, player);
                break;
            case Edit:
                EditAction.handle(house, player);
                break;
           // case Finish:
                //EditAction.handleFinish(player);
                //break;
        }
    }

    public static class InvalidActionException extends Exception {
        public InvalidActionException(String message) {
            super(message);
        }
    }
}
