package com.grandtheftwarzone.gtwhouses.client.ui.panels;

import com.grandtheftwarzone.gtwhouses.client.houseimages.HouseImage;
import com.grandtheftwarzone.gtwhouses.client.houseimages.HouseImagesManager;
import com.grandtheftwarzone.gtwhouses.client.network.GTWNetworkHandler;
import com.grandtheftwarzone.gtwhouses.common.HouseActions;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.data.Marker;
import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.HouseActionC2SPacket;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.utils.GuiTextureSprite;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HousePanel extends GuiPanel {
    public HousePanel(House house) {
        Marker marker = Marker.fromHouse(house);
        addClickListener((x, y, button) ->
                getParent().getParent().getChildComponents().forEach(c -> {
                    if (c instanceof MapPanel) {
                        MapPanel mapPanel = (MapPanel) c;
                        mapPanel.selectMarker(marker);
                    }
                })
        );

        GuiPanel imagePanel = new GuiPanel();
        imagePanel.setCssClass("imagePanel");
        //imagePanel.add(new GuiLabel("Image").setCssId("image"));

        HouseImage houseImage = HouseImagesManager.getImage(house);

        if (houseImage != null)
            imagePanel.getStyle().setTexture(new GuiTextureSprite(houseImage.getTexture(), 0, 0, houseImage.getWidth(), houseImage.getHeight(), houseImage.getWidth(), houseImage.getHeight()));
        add(imagePanel);


        String price =
                house.isForSale() ? "$" + (house.isOwned() ? house.getSellCost() : house.getBuyCost())
                        : house.isRentable() ? "$" + house.getRentCost() + "/day" : "Unavailable";

        if (price.contains("."))
            price = price.substring(0, price.indexOf('.'));

        GuiPanel pricePanel = new GuiPanel();
        pricePanel.setCssClass("pricePanel");
        pricePanel.add(new GuiLabel(price).setCssClass("price"));


        String action = house.isForSale() ? "Buy" : house.isRentable() ? "Rent" : "Unavailable";
        String buttonCss = house.isForSale() ? "buyButton" : house.isRentable() ? "rentButton" : "unavailableButton";


        GuiButton buyButton = new GuiButton().setText(action);
        buyButton.setCssClass(buttonCss);
        buyButton.addClickListener((mouseX, mouseY, mouseButton) -> {
            //TODO: Add confirm
            if (house.isForSale())
                GTWNetworkHandler.sendToServer(new HouseActionC2SPacket(HouseActions.Buy, house.getName(), null));
            else if (house.isRentable())
                GTWNetworkHandler.sendToServer(new HouseActionC2SPacket(HouseActions.Rent, house.getName(), null));

            if (house.isForSale() || house.isRentable())
                Minecraft.getMinecraft().player.closeScreen();
        });
        pricePanel.add(buyButton);


        add(pricePanel);
        add(new GuiLabel(house.getName()).setCssClass("houseName"));
    }
}
