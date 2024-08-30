package com.grandtheftwarzone.gtwhouses.client.ui.panels;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.textarea.GuiLabel;

import java.util.List;

public class MyHousesPanel extends GuiPanel {

    public MyHousesPanel(List<House> houses) {
        GuiPanel panel = new GuiPanel();
        panel.setCssClass("tabContent");
        panel.add(new GuiPanel().setCssId("bar"));

        GuiPanel contentPanel = new GuiPanel();
        contentPanel.setCssClass("tabCenterContent");

        GuiScrollPane scrollPane = new GuiScrollPane();
        scrollPane.setCssId("myHouseScrollPane");
        scrollPane.setLayout(GridLayout.columnLayout(100, 10));

        if (houses.isEmpty()) {
            contentPanel.add(new GuiLabel("You don't own any houses!").setCssId("noHousesLabel"));
            panel.add(contentPanel);
            add(panel);
            return;
        }

        for (House house : houses) {
            scrollPane.add(new MyHousePanel(house).setCssClass("myHousePanel"));
        }

        contentPanel.add(scrollPane);
        panel.add(contentPanel);

        add(panel);
    }
}
