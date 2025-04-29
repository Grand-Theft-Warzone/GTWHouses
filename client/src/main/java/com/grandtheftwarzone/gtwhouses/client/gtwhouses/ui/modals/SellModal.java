package com.grandtheftwarzone.gtwhouses.client.gtwhouses.ui.modals;

import com.grandtheftwarzone.gtwhouses.client.GTWHousesUI;
import com.grandtheftwarzone.gtwhouses.client.gtwhouses.network.GTWNetworkHandler;
import com.grandtheftwarzone.gtwhouses.common.HouseActions;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.HouseActionC2SPacket;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiIntegerField;
import fr.aym.acsguis.component.textarea.GuiLabel;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class SellModal extends GuiFrame {

    public SellModal(House house, GuiPanel parent) {
        super(new GuiScaler.Identity());
        setCssClass("modal");

        GuiPanel contentPanel = new GuiPanel();
        contentPanel.setCssClass("sellModal");
        contentPanel.setLayout(GridLayout.columnLayout(15, 15));

        GuiLabel title = new GuiLabel("Sell House");
        title.setCssId("sellTitle");

        GuiIntegerField moneyField = new GuiIntegerField(1, Integer.MAX_VALUE);
        moneyField.setCssId("moneyField");

        GuiPanel buttonPanel = new GuiPanel();
        buttonPanel.setLayout(new GridLayout(75, 15, 15, GridLayout.GridDirection.HORIZONTAL, 3));
        buttonPanel.setCssClass("buttonPanel");

        GuiButton sellButton = new GuiButton("Sell");
        sellButton.setCssClass("yesButton");
        sellButton.addClickListener((a, b, c) -> {
            GTWNetworkHandler.sendToServer(new HouseActionC2SPacket(HouseActions.Sell, house.getName(), new int[]{moneyField.getValue()}));
            Minecraft.getMinecraft().player.closeScreen();
        });

        GuiButton cancelButton = new GuiButton("Cancel");
        cancelButton.setCssClass("noButton");
        cancelButton.addClickListener((a, b, c) -> {
            parent.remove(this);
        });

        buttonPanel.add(sellButton);
        buttonPanel.add(cancelButton);

        contentPanel.add(title);
        contentPanel.add(moneyField);
        contentPanel.add(buttonPanel);

        add(contentPanel);
    }

    @Override
    public List<ResourceLocation> getCssStyles() {
        return Collections.emptyList();
    }

    @Override
    public boolean needsCssReload() {
        return GTWHousesUI.UI_RELOAD;
    }
}
