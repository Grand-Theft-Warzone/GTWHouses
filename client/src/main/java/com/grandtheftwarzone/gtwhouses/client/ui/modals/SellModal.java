package com.grandtheftwarzone.gtwhouses.client.ui.modals;

import com.grandtheftwarzone.gtwhouses.client.network.GTWNetworkHandler;
import com.grandtheftwarzone.gtwhouses.common.HouseActions;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.HouseActionC2SPacket;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.textarea.GuiIntegerField;
import fr.aym.acsguis.component.textarea.GuiLabel;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class SellModal extends GuiFrame {

    public SellModal(House house) {
        super(new GuiScaler.Identity());
        setLayout(GridLayout.columnLayout(25, 10));
        setCssClass("modal");

        GuiLabel title = new GuiLabel("Rent House");
        title.setCssId("rentTitle");

        GuiIntegerField moneyField = new GuiIntegerField(1, Integer.MAX_VALUE);
        moneyField.setCssId("moneyField");

        GuiButton sellButton = new GuiButton("Sell");
        sellButton.setCssId("rentButton");
        sellButton.addClickListener((a, b, c) -> {
            GTWNetworkHandler.sendToServer(new HouseActionC2SPacket(HouseActions.Sell, house.getName(), new int[]{moneyField.getValue()}));
            Minecraft.getMinecraft().player.closeScreen();
        });

        GuiButton cancelButton = new GuiButton("Cancel");
        cancelButton.setCssId("cancelButton");
        cancelButton.addClickListener((a, b, c) -> {
            Minecraft.getMinecraft().player.closeScreen();
        });

        add(title);
        add(moneyField);
        add(sellButton);
    }

    @Override
    public List<ResourceLocation> getCssStyles() {
        return Collections.emptyList();
    }

    @Override
    public boolean needsCssReload() {
        return true;
    }
}
