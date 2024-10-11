package com.grandtheftwarzone.gtwhouses.client.ui.frames;

import com.grandtheftwarzone.gtwhouses.client.network.GTWNetworkHandler;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.data.HouseBlock;
import com.grandtheftwarzone.gtwhouses.common.data.HouseType;
import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.CreateHouseC2SPacket;
import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.HouseCoordsC2SPacket;
import com.grandtheftwarzone.gtwhouses.common.network.packets.s2c.HouseCoordsS2CPacket;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiIntegerField;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.component.textarea.GuiTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminCreateHouseFrame extends GuiFrame {

    public static final ResourceLocation CSS_LOCATION = new ResourceLocation("gtwhousesui", "css/admin_create_house.css");

    private final GuiTextField nameField;
    private final GuiIntegerField buyCostField;
    private final GuiIntegerField rentCostField;
    private final GuiTextField imageURLField;

    private final GuiLabel minCoordsLabel;
    private final GuiLabel maxCoordsLabel;
    private final GuiLabel protectedBlocksLabel;

    private House house;
    //private List<HouseBlock> blocks;
    private int blockCount = 0;

    private HouseType type = HouseType.HIGH_END;

    GuiButton highButton = new GuiButton("High End");
    GuiButton midButton = new GuiButton("Middle End");
    GuiButton lowButton = new GuiButton("Low End");

    private String originalName = "";

    GuiButton createButton;

    public AdminCreateHouseFrame() {
        super(new GuiScaler.Identity());

        setCssId("adminCreateHouseFrame");

        GuiLabel titleLabel = new GuiLabel("Create House");
        titleLabel.setCssId("titleLabel").setCssClass("auto");
        add(titleLabel);

        GuiPanel leftPanel = new GuiPanel();
        leftPanel.setCssId("leftPanel").setCssClass("auto");
        leftPanel.setLayout(new GridLayout(-1, 30, 10, GridLayout.GridDirection.VERTICAL, 7));

        // Name
        GuiPanel namePanel = new GuiPanel();
        namePanel.setCssId("namePanel").setCssClass("auto");

        GuiLabel nameLabel = new GuiLabel("Name");
        nameLabel.setCssId("nameLabel").setCssClass("auto");

        nameField = new GuiTextField();
        nameField.setCssId("nameField").setCssClass("auto").setCssClass("input");

        namePanel.add(nameLabel);
        namePanel.add(nameField);

        // Buy cost
        GuiPanel buyCostPanel = new GuiPanel();
        buyCostPanel.setCssId("buyCostPanel").setCssClass("auto");

        GuiLabel buyCostLabel = new GuiLabel("Buy Cost");
        buyCostLabel.setCssId("buyCostLabel").setCssClass("auto");

        buyCostField = new GuiIntegerField(1, Integer.MAX_VALUE);
        buyCostField.setCssId("buyCostField").setCssClass("auto").setCssClass("input");

        buyCostPanel.add(buyCostLabel);
        buyCostPanel.add(buyCostField);

        // Rent cost
        GuiPanel rentCostPanel = new GuiPanel();
        rentCostPanel.setCssId("rentCostPanel").setCssClass("auto");

        GuiLabel rentCostLabel = new GuiLabel("Rent Cost");
        rentCostLabel.setCssId("rentCostLabel").setCssClass("auto");

        rentCostField = new GuiIntegerField(1, Integer.MAX_VALUE);
        rentCostField.setCssId("rentCostField").setCssClass("auto").setCssClass("input");

        rentCostPanel.add(rentCostLabel);
        rentCostPanel.add(rentCostField);

        //Image URL
        GuiPanel imageURLPanel = new GuiPanel();
        imageURLPanel.setCssId("imageURLPanel").setCssClass("auto");

        GuiLabel imageURLLabel = new GuiLabel("Image URL");
        imageURLLabel.setCssId("imageURLLabel").setCssClass("auto");

        imageURLField = new GuiTextField();
        imageURLField.setCssId("imageURLField").setCssClass("auto").setCssClass("input");

        imageURLPanel.add(imageURLLabel);
        imageURLPanel.add(imageURLField);

        // Buttons
        GuiPanel buttonPanel = new GuiPanel();
        buttonPanel.setLayout(new GridLayout(100, 15, 15, GridLayout.GridDirection.HORIZONTAL, 3));
        buttonPanel.setCssId("buttonPanel").setCssClass("auto");

        createButton = new GuiButton("Create");
        createButton.setCssId("createButton").setCssClass("auto");

        GuiButton cancelButton = new GuiButton("Cancel");
        cancelButton.setCssId("cancelButton").setCssClass("auto");

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        leftPanel.add(namePanel);
        leftPanel.add(buyCostPanel);
        leftPanel.add(rentCostPanel);
        leftPanel.add(imageURLPanel);
        leftPanel.add(buttonPanel);

        add(leftPanel);

        // Right panel
        GuiPanel rightPanel = new GuiPanel();
        rightPanel.setCssId("rightPanel").setCssClass("auto");
        rightPanel.setLayout(new GridLayout(-1, 25, 10, GridLayout.GridDirection.VERTICAL, 3));

        GuiLabel houseCoordsLabel = new GuiLabel("House Coords");
        houseCoordsLabel.setCssId("houseCoordsLabel").setCssClass("auto");

        GuiPanel coordsPanel = new GuiPanel();
        rightPanel.setLayout(new GridLayout(-1, 25, 10, GridLayout.GridDirection.VERTICAL, 5));
        coordsPanel.setCssId("coordsPanel").setCssClass("auto");

        minCoordsLabel = new GuiLabel("Min: Not set");
        minCoordsLabel.setCssId("minCoordsLabel").setCssClass("auto");

        maxCoordsLabel = new GuiLabel("Max: Not set");
        maxCoordsLabel.setCssId("maxCoordsLabel").setCssClass("auto");

        protectedBlocksLabel = new GuiLabel("House Blocks: Not set");
        protectedBlocksLabel.setCssId("protectedBlocksLabel").setCssClass("auto");

        GuiButton setCoordsButton = new GuiButton("Set Coords");
        setCoordsButton.setCssId("setCoordsButton").setCssClass("auto");

        GuiPanel typePanel = new GuiPanel();
        typePanel.setLayout(new GridLayout(60, 15, 5, GridLayout.GridDirection.HORIZONTAL, 3));
        typePanel.setCssId("typePanel").setCssClass("auto");

        highButton.setCssClass("selected");

        highButton.addClickListener((a, b, c) -> {
            type = HouseType.HIGH_END;
            highButton.setCssClass("selected");
            midButton.setCssClass("unselected");
            lowButton.setCssClass("unselected");
        });

        midButton.addClickListener((a, b, c) -> {
            type = HouseType.MIDDLE_END;
            highButton.setCssClass("unselected");
            midButton.setCssClass("selected");
            lowButton.setCssClass("unselected");
        });

        lowButton.addClickListener((a, b, c) -> {
            type = HouseType.LOW_END;
            highButton.setCssClass("unselected");
            midButton.setCssClass("unselected");
            lowButton.setCssClass("selected");
        });

        typePanel.add(highButton);
        typePanel.add(midButton);
        typePanel.add(lowButton);


        setCoordsButton.addClickListener((a, b, c) -> {
            GTWNetworkHandler.sendToServer(new HouseCoordsC2SPacket(nameField.getText(), buyCostField.getValue(), rentCostField.getValue(), type.ordinal(), imageURLField.getText(), originalName));
            Minecraft.getMinecraft().player.closeScreen();
        });

        cancelButton.addClickListener((a, b, c) -> {
            Minecraft.getMinecraft().player.closeScreen();
        });

        createButton.addClickListener((a, b, c) -> {
            if (house == null || blockCount == 0/*|| blocks == null*/) return;

            house.setName(nameField.getText());
            house.setBuyCost(buyCostField.getValue());
            house.setRentCost(rentCostField.getValue());
            house.setImageURL(imageURLField.getText().trim());

            GTWNetworkHandler.sendToServer(new CreateHouseC2SPacket(house, originalName/*, blocks*/));
            Minecraft.getMinecraft().player.closeScreen();
        });

        coordsPanel.add(minCoordsLabel);
        coordsPanel.add(maxCoordsLabel);
        coordsPanel.add(protectedBlocksLabel);
        coordsPanel.add(setCoordsButton);

        rightPanel.add(houseCoordsLabel);
        rightPanel.add(coordsPanel);
        rightPanel.add(typePanel);

        add(rightPanel);
    }

    public void loadPacket(HouseCoordsS2CPacket packet) {
        this.originalName = packet.getOriginalName();

        house = new House(nameField.getText(), packet.getWorldName(), rentCostField.getValue(), buyCostField.getValue(), type);
        house.setMinPosX(packet.getMinX());
        house.setMaxPosX(packet.getMaxX());
        house.setMinPosY(packet.getMinY());
        house.setMaxPosY(packet.getMaxY());
        house.setMinPosZ(packet.getMinZ());
        house.setMaxPosZ(packet.getMaxZ());
        house.setType(HouseType.values()[packet.getType()]);
        house.setImageURL(packet.getImageURL());

        type = house.getType();

        switch (type) {
            case HIGH_END:
                highButton.setCssClass("selected");
                midButton.setCssClass("unselected");
                lowButton.setCssClass("unselected");
                break;
            case MIDDLE_END:
                highButton.setCssClass("unselected");
                midButton.setCssClass("selected");
                lowButton.setCssClass("unselected");
                break;
            case LOW_END:
                highButton.setCssClass("unselected");
                midButton.setCssClass("unselected");
                lowButton.setCssClass("selected");
                break;
        }

        if (!originalName.isEmpty()) createButton.setText("Edit House");

        // blocks = packet.getBlocks();
        blockCount = packet.getBlockCount();

        nameField.setText(packet.getName());
        buyCostField.setValue(packet.getBuyCost());
        rentCostField.setValue(packet.getRentCost());
        imageURLField.setText(house.getImageURL());

        minCoordsLabel.setText("Min: " + packet.getMinX() + "X, " + packet.getMinY() + "Y, " + packet.getMinZ() + "Z");
        maxCoordsLabel.setText("Max: " + packet.getMaxX() + "X, " + packet.getMaxY() + "Y, " + packet.getMaxZ() + "Z");
        //protectedBlocksLabel.setText("House Blocks: " + packet.getBlocks().size());
        protectedBlocksLabel.setText("House Blocks: " + packet.getBlockCount());
    }

    @Override
    public List<ResourceLocation> getCssStyles() {
        return Collections.singletonList(CSS_LOCATION);
    }

    @Override
    public boolean needsCssReload() {
        return true;
    }

    @Override
    public boolean doesPauseGame() {
        return true;
    }


}
