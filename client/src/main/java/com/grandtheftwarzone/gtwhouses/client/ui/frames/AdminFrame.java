package com.grandtheftwarzone.gtwhouses.client.ui.frames;

import com.grandtheftwarzone.gtwhouses.client.ui.panels.AdminHousePanel;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.textarea.GuiLabel;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AdminFrame extends GuiFrame {
    public static final ResourceLocation CSS = new ResourceLocation("gtwhousesui", "css/admin_frame.css");

    public AdminFrame(Collection<House> houses) {
        super(new GuiScaler.Identity());
        setCssId("adminFrame");

        GuiLabel titleLabel = new GuiLabel("Admin Panel");
        titleLabel.setCssId("titleLabel");
        add(titleLabel);

        GuiPanel buttonPanel = new GuiPanel();
        buttonPanel.setCssId("buttonPanel");
        buttonPanel.setLayout(new GridLayout(-1, 30, 10, GridLayout.GridDirection.VERTICAL, 5));

        GuiButton createHouseButton = new GuiButton("Create House");
        createHouseButton.setCssId("createHouseButton");
        buttonPanel.add(createHouseButton);

        createHouseButton.addClickListener((mouseX, mouseY, mouseButton) -> {
            Minecraft.getMinecraft().player.closeScreen();
            ACsGuiApi.asyncLoadThenShowGui("admin_create_house.css", AdminCreateHouseFrame::new);
        });
        add(buttonPanel);


        GuiScrollPane scrollPane = new GuiScrollPane();
        scrollPane.setCssId("housesPanel");

        for (House house : houses) {
            scrollPane.add(new AdminHousePanel());
        }

        add(scrollPane);
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
