package com.grandtheftwarzone.gtwhouses.client.ui.panels;

import com.grandtheftwarzone.gtwhouses.client.houseimages.HouseImage;
import com.grandtheftwarzone.gtwhouses.client.houseimages.HouseImagesManager;
import com.grandtheftwarzone.gtwhouses.client.network.GTWNetworkHandler;
import com.grandtheftwarzone.gtwhouses.client.ui.modals.ConfirmModal;
import com.grandtheftwarzone.gtwhouses.client.ui.modals.SellModal;
import com.grandtheftwarzone.gtwhouses.common.GTWHousesUtils;
import com.grandtheftwarzone.gtwhouses.common.HouseActions;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.HouseActionC2SPacket;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.utils.GuiTextureSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.UUID;

public class RentingHousePanel extends GuiPanel {

    public RentingHousePanel(House house) {
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

        String rentValue = house.getRentCost() + "$/day";
        NetworkPlayerInfo playerInfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(house.getOwner());
        String ownerName = house.getOwner() == null ? "NO OWNER" : playerInfo == null ? "UNKNOWN" : playerInfo.getGameProfile().getName();

        houseInfoPanel.add(new GuiLabel("Renting: " + house.getName()).setCssId("houseName"));
        houseInfoPanel.add(new GuiLabel("Buy Value: " + house.getBuyCost() + "$").setCssId("housePrice"));
        houseInfoPanel.add(new GuiLabel("Owner: " + ownerName).setCssId("housePrice"));
        houseInfoPanel.add(new GuiLabel("Rent Value: " + rentValue).setCssId("houseLocation"));

        GuiPanel buttonsPanel = new GuiPanel();
        buttonsPanel.setLayout(new GridLayout(50, 15, 5, GridLayout.GridDirection.HORIZONTAL, 4));
        buttonsPanel.setCssClass("houseButtons");

        GuiButton stopRentButton = new GuiButton("Stop Renting");
        stopRentButton.setCssId("stopRentButton").setCssClass("houseBtn");

        stopRentButton.addClickListener((pos, mouse, button) ->
                add(new ConfirmModal(this, (x, y, pointer) ->
                        GTWNetworkHandler.sendToServer(new HouseActionC2SPacket(HouseActions.Unrent, house.getName(), null))
                ))
        );

        buttonsPanel.add(stopRentButton);

        contentPanel.add(houseInfoPanel);
        contentPanel.add(buttonsPanel);

        add(contentPanel);
    }

}
