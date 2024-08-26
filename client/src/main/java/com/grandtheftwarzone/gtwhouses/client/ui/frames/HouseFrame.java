package com.grandtheftwarzone.gtwhouses.client.ui.frames;

import com.grandtheftwarzone.gtwhouses.client.GTWHousesUI;
import com.grandtheftwarzone.gtwhouses.client.ui.panels.HousePanel;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.panel.GuiTabbedPane;
import fr.aym.acsguis.component.textarea.GuiLabel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class HouseFrame extends GuiFrame {
    public static final ResourceLocation CSS_LOCATION = new ResourceLocation(GTWHousesUI.MODID, "css/house_frame.css");

    Collection<House> houses;

    public HouseFrame(Collection<House> houses) {
        super(new GuiScaler.Identity());
        this.houses = houses;

        setCssClass("frame");

        add(new GuiLabel("Grand Theft Warzone").setCssId("titleId"));

        GuiTabbedPane tabbedPane = new GuiTabbedPane();
        tabbedPane.getStyle().getYPos().setAbsolute(80);
        tabbedPane.setCssClass("tabbedPane");

        GuiPanel panel = new GuiPanel();
        panel.setCssClass("tabContent");
        panel.add(new GuiPanel().setCssId("bar"));

        GuiPanel contentPanel = new GuiPanel();
        contentPanel.setCssClass("tabCenterContent");

        GuiScrollPane scrollPane = new GuiScrollPane();
        scrollPane.setCssId("houseScrollPane");
        scrollPane.setLayout(GridLayout.columnLayout(200, 10));

        for(House house : houses)
            scrollPane.add(new HousePanel(house).setCssClass("housePanel"));

        contentPanel.add(scrollPane);
        contentPanel.add(new GuiPanel().setCssId("map"));

        panel.add(contentPanel);

        //Other tabs
        GuiPanel tabPanel = new GuiPanel();
        panel.add(new GuiPanel().setCssId("bar"));
        tabPanel.setCssClass("tabContent");

        tabbedPane.addTab("ALL", panel);
        tabbedPane.addTab("HIGH-END", tabPanel);
        tabbedPane.addTab("MEDIUM", tabPanel);
        tabbedPane.addTab("LOW-END", tabPanel);
        tabbedPane.addTab("NEW", tabPanel);

        tabbedPane.selectTab(0);
        add(tabbedPane);
    }

    @Override
    public List<ResourceLocation> getCssStyles() {
        return Collections.singletonList(CSS_LOCATION);
    }

    @Override
    public boolean needsCssReload() {
        return true;
    }
}
