package com.grandtheftwarzone.gtwhouses.client.ui.modals;

import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiIntegerField;
import fr.aym.acsguis.component.textarea.GuiLabel;

public class RentModal extends GuiPanel {
    public RentModal(GuiPanel parent) {
        setLayout(GridLayout.columnLayout(25, 10));
        setCssClass("modal");

        GuiLabel title = new GuiLabel("Rent House");
        title.setCssId("rentTitle");

        GuiIntegerField moneyField = new GuiIntegerField(1, Integer.MAX_VALUE);
        moneyField.setCssId("moneyField");

        GuiButton rentButton = new GuiButton("Rent");
        rentButton.setCssId("rentButton");
        rentButton.addClickListener((a, b ,c ) -> {
            parent.remove(this);
        });

        add(title);
        add(moneyField);
        add(rentButton);
    }
}
