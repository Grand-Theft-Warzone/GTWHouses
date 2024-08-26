package com.grandtheftwarzone.gtwhouses.client.ui.panels;

import com.grandtheftwarzone.gtwhouses.client.ui.modals.RentModal;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiLabel;

public class MyHousePanel extends GuiPanel {

    public MyHousePanel() {
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
        houseInfoPanel.add(new GuiLabel("Renting: 200$/day").setCssId("houseLocation"));


        GuiPanel buttonsPanel = new GuiPanel();
        buttonsPanel.setLayout(new GridLayout(50, 15, 5, GridLayout.GridDirection.HORIZONTAL, 3));
        buttonsPanel.setCssClass("houseButtons");


        GuiButton sellButton = new GuiButton("Sell");
        sellButton.setCssId("sellButton").setCssClass("houseBtn");

        GuiButton rentOutButton = new GuiButton("Rent Out");
        rentOutButton.setCssId("rentOutButton").setCssClass("houseBtn");
        rentOutButton.addClickListener((a, b, c) -> add(new RentModal(this)));

        buttonsPanel.add(sellButton);
        buttonsPanel.add(rentOutButton);


        contentPanel.add(houseInfoPanel);
        contentPanel.add(buttonsPanel);

        add(contentPanel);
    }

}
