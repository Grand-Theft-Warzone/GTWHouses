package com.grandtheftwarzone.gtwhouses.client.ui.panels;

import com.grandtheftwarzone.gtwhouses.client.ui.frames.HouseFrame;
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
            scrollPane.add(new MyHousePanel(houses.get(i)).setCssClass("myHousePanel"));
        }

        contentPanel.add(scrollPane);
        panel.add(contentPanel);

        add(panel);
    }
}
