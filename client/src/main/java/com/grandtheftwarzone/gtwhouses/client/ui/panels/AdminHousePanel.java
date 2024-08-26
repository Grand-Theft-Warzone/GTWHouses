package com.grandtheftwarzone.gtwhouses.client.ui.panels;

import com.grandtheftwarzone.gtwhouses.client.ui.modals.ConfirmModal;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiLabel;

public class AdminHousePanel extends GuiPanel {

    public AdminHousePanel() {
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

        houseInfoPanel.add(new GuiLabel("House Name").setCssId("houseName"));
        houseInfoPanel.add(new GuiLabel("Value: 3,000,000$").setCssId("housePrice"));
        houseInfoPanel.add(new GuiLabel("Selling: NOT FOR SALE").setCssId("houseSell"));
        houseInfoPanel.add(new GuiLabel("Renter: NO RENTER").setCssId("houseRenter"));
      //  houseInfoPanel.add(new GuiLabel("Renting: 200$/day").setCssId("houseLocation"));
        houseInfoPanel.add(new GuiLabel("Owner: Playerxx").setCssId("houseOwner"));


        GuiPanel buttonsPanel = new GuiPanel();
        buttonsPanel.setLayout(new GridLayout(75, 15, 5, GridLayout.GridDirection.HORIZONTAL, 3));
        buttonsPanel.setCssClass("houseButtons");


        GuiButton resetHouseBlocks = new GuiButton("Reset Blocks");
        resetHouseBlocks.setCssId("resetButton").setCssClass("houseBtn");
        resetHouseBlocks.addClickListener((a, b, c) -> {add (new ConfirmModal(this));});

        GuiButton deleteHouse = new GuiButton("Delete");
        deleteHouse.setCssId("deleteButton").setCssClass("houseBtn");
        //rentOutButton.addClickListener((a, b, c) -> add(new RentModal(this)));
        deleteHouse.addClickListener((a, b, c) -> {add (new ConfirmModal(this));});

        buttonsPanel.add(resetHouseBlocks);
        buttonsPanel.add(deleteHouse);


        contentPanel.add(houseInfoPanel);
        contentPanel.add(buttonsPanel);

        add(contentPanel);
    }

}
