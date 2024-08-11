package com.grandtheftwarzone.gtwhouses.client.ui.frames;

import com.grandtheftwarzone.gtwhouses.client.GTWHousesUI;
import com.grandtheftwarzone.gtwhouses.client.ui.panels.MyHousePanel;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.textarea.GuiLabel;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MyHousesFrame extends GuiFrame {
    public static final ResourceLocation CSS_LOCATION = new ResourceLocation(GTWHousesUI.MODID, "css/my_house_frame.css");

    public MyHousesFrame() {
        super(new GuiScaler.Identity());
        setCssId("myHousesFrame");

        add(new GuiLabel("Grand Theft Warzone").setCssId("titleId"));

        GuiPanel contentPanel = new GuiPanel();
        contentPanel.setCssClass("content");
        contentPanel.setLayout(GridLayout.columnLayout(200, 10));

        contentPanel.add(new GuiLabel("My Houses").setCssId("myHousesTitle"));

        GuiScrollPane scrollPane = new GuiScrollPane();
        scrollPane.setCssId("houseScrollPane");
        scrollPane.setLayout(GridLayout.columnLayout(100, 10));

        for (int i = 0; i < 10; i++)
            scrollPane.add(new MyHousePanel().setCssClass("myHousePanel"));


        contentPanel.add(scrollPane);

        add(contentPanel);
    }


    @Override
    public List<ResourceLocation> getCssStyles() {
        ArrayList<ResourceLocation> css = new ArrayList<>();
        css.add(CSS_LOCATION);
        css.add(HouseFrame.CSS_LOCATION);
        return css;
    }

    @Override
    public boolean needsCssReload() {
        return true;
    }
}
