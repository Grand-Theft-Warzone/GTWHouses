package com.grandtheftwarzone.gtwhouses.client.ui.panels;

import com.grandtheftwarzone.gtwhouses.client.network.GTWNetworkHandler;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.BuyHouseC2SPacket;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiLabel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HousePanel extends GuiPanel {
    public HousePanel(House house) {
        GuiPanel imagePanel = new GuiPanel();
        imagePanel.setCssClass("imagePanel");
        imagePanel.add(new GuiLabel("Image").setCssId("image"));
        add(imagePanel);

        GuiPanel pricePanel = new GuiPanel();
        pricePanel.setCssClass("pricePanel");
        pricePanel.add(new GuiLabel("$"+house.getBuyCost()).setCssClass("price"));

        GuiButton buyButton = new GuiButton().setText("Buy");
        buyButton.setCssClass("buyButton");
        buyButton.addClickListener((mouseX, mouseY, mouseButton) -> {
            //TODO: Add confirm
            GTWNetworkHandler.sendToServer(new BuyHouseC2SPacket(house.getName()));
        });
        pricePanel.add(buyButton);



        add(pricePanel);
        add(new GuiLabel(house.getName()).setCssClass("houseName"));
    }
}
