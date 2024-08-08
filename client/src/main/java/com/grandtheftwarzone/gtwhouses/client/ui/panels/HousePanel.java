package com.grandtheftwarzone.gtwhouses.client.ui.panels;

import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiLabel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HousePanel extends GuiPanel {
    public HousePanel() {
        GuiPanel imagePanel = new GuiPanel();
        imagePanel.setCssClass("imagePanel");
        imagePanel.add(new GuiLabel("Image").setCssId("image"));
        add(imagePanel);

        GuiPanel pricePanel = new GuiPanel();
        pricePanel.setCssClass("pricePanel");
        pricePanel.add(new GuiLabel("$9,999,999").setCssClass("price"));
        pricePanel.add(new GuiButton().setText("Buy").setCssClass("buyButton"));

        add(pricePanel);
        add(new GuiLabel("House 2").setCssClass("houseName"));
    }
}
