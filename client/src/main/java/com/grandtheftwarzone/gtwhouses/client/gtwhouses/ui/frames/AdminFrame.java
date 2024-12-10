package com.grandtheftwarzone.gtwhouses.client.gtwhouses.ui.frames;

import com.grandtheftwarzone.gtwhouses.client.gtwhouses.ui.panels.AdminHousePanel;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.component.textarea.GuiTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AdminFrame extends GuiFrame {
    public static final ResourceLocation CSS = new ResourceLocation("gtwhousesui", "css/admin_frame.css");

    private final GuiScrollPane scrollPane;

    public AdminFrame(Collection<House> houses) {
        super(new GuiScaler.Identity());
        setCssId("adminFrame");

        GuiLabel titleLabel = new GuiLabel("Admin Panel");
        titleLabel.setCssId("titleLabel");
        add(titleLabel);

        GuiPanel buttonPanel = new GuiPanel();
        buttonPanel.setCssId("buttonPanel");

        GuiTextField searchField = new GuiTextField();
        searchField.setCssId("searchField");

        GuiButton searchButton = new GuiButton("Search");
        searchButton.setCssId("searchButton");

        buttonPanel.add(searchField);
        buttonPanel.add(searchButton);

        searchButton.addClickListener((mouseX, mouseY, mouseButton) -> {
            renderHouses(houses
                    .stream()
                    .filter(house -> house.getName().toLowerCase().contains(searchField.getText().toLowerCase()))
                    .collect(Collectors.toList())
            );
        });

        GuiButton createHouseButton = new GuiButton("Create House");
        createHouseButton.setCssId("createHouseButton");
        buttonPanel.add(createHouseButton);


        createHouseButton.addClickListener((mouseX, mouseY, mouseButton) -> {
            Minecraft.getMinecraft().player.closeScreen();
            ACsGuiApi.asyncLoadThenShowGui("admin_create_house.css", AdminCreateHouseFrame::new);
        });
        add(buttonPanel);


        GuiPanel contentPanel = new GuiPanel();
        contentPanel.setCssId("housesPanel");
        scrollPane = new GuiScrollPane();
        scrollPane.setCssId("housesPanelScroll");
        //scrollPane.setCssId("housesPanel");
        scrollPane.setLayout(GridLayout.columnLayout(140, 10));

        renderHouses(houses);

        contentPanel.add(scrollPane);
        add(contentPanel);
    }

    private void renderHouses(Collection<House> houses) {
        scrollPane.removeAllChildren();
        for (House house : houses) scrollPane.add(new AdminHousePanel(house));

    }


    @Override
    public List<ResourceLocation> getCssStyles() {
        return Collections.singletonList(CSS);
    }

    @Override
    public boolean needsCssReload() {
        return true;
    }
}
