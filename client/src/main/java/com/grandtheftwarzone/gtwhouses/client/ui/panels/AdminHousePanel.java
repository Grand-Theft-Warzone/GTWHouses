package com.grandtheftwarzone.gtwhouses.client.ui.panels;

import com.grandtheftwarzone.gtwhouses.client.GTWHousesUI;
import com.grandtheftwarzone.gtwhouses.client.network.GTWNetworkHandler;
import com.grandtheftwarzone.gtwhouses.client.ui.modals.ConfirmModal;
import com.grandtheftwarzone.gtwhouses.common.HouseActions;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.HouseActionC2SPacket;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.utils.GuiTextureSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class AdminHousePanel extends GuiPanel {

    public AdminHousePanel(House house) {
        setCssClass("adminHousePanel");

        GuiPanel imagePanel = new GuiPanel();
        imagePanel.setCssClass("myHouseImage");
        // imagePanel.add(new GuiLabel("Image").setCssId("image"));

        if (house.getImageURL() != null)
            imagePanel.getStyle().setTexture(new GuiTextureSprite(new ResourceLocation(house.getImageURL())));

        add(imagePanel);

        GuiPanel contentPanel = new GuiPanel();
        contentPanel.setCssClass("houseContent");

        GuiPanel houseInfoPanel = new GuiPanel();
        houseInfoPanel.setCssClass("houseInfo");
        houseInfoPanel.setLayout(GridLayout.columnLayout(15, 2));


        String selling = house.isForSale() ? "FOR SALE" : "NOT FOR SALE";
        String rentValue = house.getRentCost() + "$/day";

        UUID renterUUID = house.getRenter() == null ? null : house.getRenter();
        String renterName = house.getRenter() == null ? "NO RENTER" : Minecraft.getMinecraft().getConnection().getPlayerInfo(renterUUID).getGameProfile().getName();

        UUID ownerUUID = house.getOwner() == null ? null : house.getOwner();
        String ownerName = house.getOwner() == null ? "NO OWNER" : Minecraft.getMinecraft().getConnection().getPlayerInfo(ownerUUID).getGameProfile().getName();

        houseInfoPanel.add(new GuiLabel(house.getName()).setCssId("houseName"));
        houseInfoPanel.add(new GuiLabel("Value: " + house.getBuyCost() + "$").setCssId("housePrice"));
        houseInfoPanel.add(new GuiLabel("Selling: " + selling).setCssId("houseSell"));
        houseInfoPanel.add(new GuiLabel("Renter: " + renterName).setCssId("houseRenter"));
        houseInfoPanel.add(new GuiLabel("Renting: " + rentValue).setCssId("houseLocation"));
        houseInfoPanel.add(new GuiLabel("Owner: " + ownerName).setCssId("houseOwner"));


        GuiPanel buttonsPanel = new GuiPanel();
        buttonsPanel.setLayout(new GridLayout(75, 15, 5, GridLayout.GridDirection.HORIZONTAL, 6));
        buttonsPanel.setCssClass("houseButtons");


        GuiButton editHouse = new GuiButton("Edit Blocks");
        editHouse.setCssId("editButton").setCssClass("houseBtn");
        editHouse.addClickListener((a, b, c) -> {
            GTWNetworkHandler.sendToServer(new HouseActionC2SPacket(HouseActions.Edit, house.getName(), null));
            Minecraft.getMinecraft().player.closeScreen();
        });


        GuiButton resetHouseBlocks = new GuiButton("Reset Blocks");
        resetHouseBlocks.setCssId("resetButton").setCssClass("houseBtn");
        resetHouseBlocks.addClickListener((a, b, c) -> {
            add(new ConfirmModal(this, (mouseX, mouseY, pointer) -> {
                GTWNetworkHandler.sendToServer(new HouseActionC2SPacket(HouseActions.Reset, house.getName(), null));
                Minecraft.getMinecraft().player.closeScreen();
            }));
        });

        GuiButton deleteHouse = new GuiButton("Delete");
        deleteHouse.setCssId("deleteButton").setCssClass("houseBtn");
        deleteHouse.addClickListener((a, b, c) -> {
            add(new ConfirmModal(this, (mouseX, mouseY, pointer) -> {
                GTWNetworkHandler.sendToServer(new HouseActionC2SPacket(HouseActions.Remove, house.getName(), null));
                Minecraft.getMinecraft().player.closeScreen();
            }));
        });

        buttonsPanel.add(editHouse);
        buttonsPanel.add(resetHouseBlocks);
        buttonsPanel.add(deleteHouse);

        contentPanel.add(houseInfoPanel);
        contentPanel.add(buttonsPanel);

        add(contentPanel);
    }

}
