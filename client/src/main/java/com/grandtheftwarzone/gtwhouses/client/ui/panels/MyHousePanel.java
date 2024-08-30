package com.grandtheftwarzone.gtwhouses.client.ui.panels;

import com.grandtheftwarzone.gtwhouses.client.network.GTWNetworkHandler;
import com.grandtheftwarzone.gtwhouses.client.ui.modals.SellModal;
import com.grandtheftwarzone.gtwhouses.common.HouseActions;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.HouseActionC2SPacket;
import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiLabel;
import net.minecraft.client.Minecraft;

import java.util.UUID;

public class MyHousePanel extends GuiPanel {

    public MyHousePanel(House house) {
        GuiPanel imagePanel = new GuiPanel();
        imagePanel.setCssClass("myHouseImage");
        // imagePanel.add(new GuiLabel("Image").setCssId("image"));
        add(imagePanel);

        GuiPanel contentPanel = new GuiPanel();
        contentPanel.setCssClass("houseContent");
        contentPanel.setLayout(GridLayout.columnLayout(50, 0));

        GuiPanel houseInfoPanel = new GuiPanel();
        houseInfoPanel.setCssClass("houseInfo");
        houseInfoPanel.setLayout(GridLayout.columnLayout(15, 2));

        UUID renterUUID = house.getRenter() == null ? null : house.getRenter();
        String renterName = house.getRenter() == null ? "NO RENTER" : Minecraft.getMinecraft().getConnection().getPlayerInfo(renterUUID).getGameProfile().getName();

        houseInfoPanel.add(new GuiLabel(house.getName()).setCssId("houseName"));
        houseInfoPanel.add(new GuiLabel("Buy Value: " + house.getBuyCost() + "$").setCssId("housePrice"));
        houseInfoPanel.add(new GuiLabel("Selling: NOT FOR SALE").setCssId("houseSell"));
        houseInfoPanel.add(new GuiLabel("Renter: " + renterName).setCssId("houseRenter"));
        houseInfoPanel.add(new GuiLabel("Rent Value: 200$/day").setCssId("houseLocation"));

        GuiPanel buttonsPanel = new GuiPanel();
        buttonsPanel.setLayout(new GridLayout(50, 15, 5, GridLayout.GridDirection.HORIZONTAL, 4));
        buttonsPanel.setCssClass("houseButtons");

        GuiButton sellButton = new GuiButton("Sell");
        sellButton.setCssId("sellButton").setCssClass("houseBtn");
        sellButton.addClickListener((pos, mouse, button) -> {
            Minecraft.getMinecraft().player.closeScreen();
            ACsGuiApi.asyncLoadThenShowGui("sellModal", () -> new SellModal(house));
        });

        GuiButton citySellButton = new GuiButton("City Sell");
        citySellButton.setCssId("citySellButton").setCssClass("houseBtn");
        citySellButton.addClickListener((pos, mouse, button) -> {
            GTWNetworkHandler.sendToServer(new HouseActionC2SPacket(HouseActions.CitySell, house.getName(), null));
            Minecraft.getMinecraft().player.closeScreen();
        });

        String rentAction = house.isRentable() ? "Stop Renting" : "Rent Out";
        GuiButton rentOutButton = new GuiButton(rentAction);
        rentOutButton.setCssId("rentOutButton").setCssClass("houseBtn");
        rentOutButton.addClickListener((pos, mouse, button) -> {
            GTWNetworkHandler.sendToServer(new HouseActionC2SPacket(
                    house.isRentable() ? HouseActions.Unrentable : HouseActions.Rentable, house.getName(), null));
            Minecraft.getMinecraft().player.closeScreen();
        });

        buttonsPanel.add(sellButton);
        buttonsPanel.add(citySellButton);
        buttonsPanel.add(rentOutButton);

        contentPanel.add(houseInfoPanel);
        contentPanel.add(buttonsPanel);

        add(contentPanel);
    }

}
