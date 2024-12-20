package com.grandtheftwarzone.gtwhouses.client.gtwhouses.ui.panels;

import com.grandtheftwarzone.gtwhouses.client.gtwhouses.ui.frames.HouseFrame;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.textarea.GuiLabel;

import java.util.List;

public class MyHousesPanel extends GuiPanel {

    public MyHousesPanel(List<House> houses, GuiPanel bar) {
        GuiPanel panel = new GuiPanel();
        panel.setCssClass("tabContent");
        panel.add(bar);

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

        for (int i = HouseFrame.sortHighToLow ? houses.size() - 1 : 0; HouseFrame.sortHighToLow ? i >= 0 : i < houses.size(); i += HouseFrame.sortHighToLow ? -1 : 1) {
            if (HouseFrame.filterType != null && !houses.get(i).getType().equals(HouseFrame.filterType)) continue;
            GuiPanel housePanel = houses.get(i).isRented() ? new RentingHousePanel(houses.get(i)) : new MyHousePanel(houses.get(i));
            housePanel.setCssClass("myHousePanel");

            scrollPane.add(housePanel);
        }

        if (!houses.isEmpty()) {
            GuiPanel emptyPanel = new GuiPanel();
            emptyPanel.setCssId("emptyPanel");
            emptyPanel.setCssClass("myHousePanel");
            scrollPane.add(emptyPanel);
        }

        contentPanel.add(scrollPane);
        panel.add(contentPanel);

        add(panel);
    }
}
