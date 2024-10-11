package com.grandtheftwarzone.gtwhouses.client.ui.panels;

import com.grandtheftwarzone.gtwhouses.client.houseimages.HouseImage;
import com.grandtheftwarzone.gtwhouses.client.houseimages.HouseImagesManager;
import com.grandtheftwarzone.gtwhouses.client.network.GTWNetworkHandler;
import com.grandtheftwarzone.gtwhouses.client.ui.modals.ConfirmModal;
import com.grandtheftwarzone.gtwhouses.client.ui.modals.SellModal;
import com.grandtheftwarzone.gtwhouses.common.HouseActions;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.HouseActionC2SPacket;
import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.utils.GuiTextureSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class MyHousePanel extends GuiPanel {

    public MyHousePanel(House house) {
        GuiPanel imagePanel = new GuiPanel();
        imagePanel.setCssClass("myHouseImage");
        // imagePanel.add(new GuiLabel("Image").setCssId("image"));

        HouseImage houseImage = HouseImagesManager.getImage(house);

        if (houseImage != null)
            imagePanel.getStyle().setTexture(new GuiTextureSprite(houseImage.getTexture(), 0, 0, houseImage.getWidth(), houseImage.getHeight(), houseImage.getWidth(), houseImage.getHeight()));

        add(imagePanel);

        GuiPanel contentPanel = new GuiPanel();
        contentPanel.setCssClass("houseContent");
        contentPanel.setLayout(GridLayout.columnLayout(50, 0));

        GuiPanel houseInfoPanel = new GuiPanel();
        houseInfoPanel.setCssClass("houseInfo");
        houseInfoPanel.setLayout(GridLayout.columnLayout(15, 2));

        String selling = house.isForSale() ? String.valueOf(house.getSellCost()) : "NOT FOR SALE";
        String rentValue = house.getRentCost() + "$/day";

        UUID renterUUID = house.getRenter() == null ? null : house.getRenter();
        String renterName = house.getRenter() == null ? "NO RENTER" : Minecraft.getMinecraft().getConnection().getPlayerInfo(renterUUID).getGameProfile().getName();

        houseInfoPanel.add(new GuiLabel(house.getName()).setCssId("houseName"));
        houseInfoPanel.add(new GuiLabel("Buy Value: " + house.getBuyCost() + "$").setCssId("housePrice"));
        houseInfoPanel.add(new GuiLabel("Selling: " + selling).setCssId("houseSell"));
        houseInfoPanel.add(new GuiLabel("Renter: " + renterName).setCssId("houseRenter"));
        houseInfoPanel.add(new GuiLabel("Rent Value: " + rentValue).setCssId("houseLocation"));

        GuiPanel buttonsPanel = new GuiPanel();
        buttonsPanel.setLayout(new GridLayout(50, 15, 5, GridLayout.GridDirection.HORIZONTAL, 4));
        buttonsPanel.setCssClass("houseButtons");

        GuiButton sellButton = new GuiButton( house.getSellCost() == -1 ?  "Sell" : "Unsell");
        sellButton.setCssId("sellButton").setCssClass("houseBtn");
        sellButton.addClickListener((pos, mouse, button) -> {
            if (house.getSellCost() == -1)
                add(new SellModal(house, this));
            else
                add(new ConfirmModal(this, (mouseX, mouseY, pointer) -> {
                    GTWNetworkHandler.sendToServer(new HouseActionC2SPacket(HouseActions.Unsell, house.getName(), null));
                    Minecraft.getMinecraft().player.closeScreen();
                }));
        });

        GuiButton citySellButton = new GuiButton("City Sell");
        citySellButton.setCssId("citySellButton").setCssClass("houseBtn");

        citySellButton.addClickListener((a, b, c) -> add(new ConfirmModal(this, (mouseX, mouseY, pointer) -> {
            GTWNetworkHandler.sendToServer(new HouseActionC2SPacket(HouseActions.CitySell, house.getName(), null));
            Minecraft.getMinecraft().player.closeScreen();
        })));

        String rentAction = house.isRentable() ? "Stop Renting" : "Rent Out";
        GuiButton rentOutButton = new GuiButton(rentAction);
        rentOutButton.setCssId("rentOutButton").setCssClass("houseBtn");
        rentOutButton.addClickListener((a, b, c) -> add(new ConfirmModal(this, (pos, mouse, button) -> {
            GTWNetworkHandler.sendToServer(new HouseActionC2SPacket(
                    house.isRentable() ? HouseActions.Unrentable : HouseActions.Rentable, house.getName(), null));
            Minecraft.getMinecraft().player.closeScreen();
        })));

        buttonsPanel.add(sellButton);
        buttonsPanel.add(citySellButton);
        buttonsPanel.add(rentOutButton);

        contentPanel.add(houseInfoPanel);
        contentPanel.add(buttonsPanel);

        add(contentPanel);
    }

}
