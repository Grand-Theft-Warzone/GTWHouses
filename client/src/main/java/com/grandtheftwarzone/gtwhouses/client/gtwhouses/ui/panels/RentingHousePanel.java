package com.grandtheftwarzone.gtwhouses.client.gtwhouses.ui.panels;

import com.grandtheftwarzone.gtwhouses.client.GTWHousesUI;
import com.grandtheftwarzone.gtwhouses.client.gtwhouses.houseimages.HouseImage;
import com.grandtheftwarzone.gtwhouses.client.gtwhouses.network.GTWNetworkHandler;
import com.grandtheftwarzone.gtwhouses.client.gtwhouses.ui.modals.ConfirmModal;
import com.grandtheftwarzone.gtwhouses.client.gtwhouses.houseimages.HouseImagesManager;
import com.grandtheftwarzone.gtwhouses.common.HouseActions;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.network.GTWHousesPacket;
import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;
import com.grandtheftwarzone.gtwhouses.common.network.packets.c2s.HouseActionC2SPacket;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.utils.GuiTextureSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RentingHousePanel extends GuiPanel {

    public RentingHousePanel(House house) {

        GTWHousesUI.getLogger().info("House: " + house.getName());
        GTWHousesUI.getLogger().info("Owner: " + house.getOwner());
        GTWHousesUI.getLogger().info("Renter: " + house.getRenter());
        GTWHousesUI.getLogger().info("Rentable: " + house.isRentable());
        GTWHousesUI.getLogger().info("Rented: " + house.isRented());
        GTWHousesUI.getLogger().info("For sale: " + house.isForSale());
        GTWHousesUI.getLogger().info("Sell cost: " + house.getSellCost());
        GTWHousesUI.getLogger().info("Rent cost: " + house.getRentCost());
        GTWHousesUI.getLogger().info("Buy cost: " + house.getBuyCost());
        GTWHousesUI.getLogger().info("Min pos: " + house.getMinPosX() + " " + house.getMinPosY() + " " + house.getMinPosZ());
        GTWHousesUI.getLogger().info("Max pos: " + house.getMaxPosX() + " " + house.getMaxPosY() + " " + house.getMaxPosZ());
        GTWHousesUI.getLogger().info("Type: " + house.getType());
        GTWHousesUI.getLogger().info("Image URL: " + house.getImageURL());
        GTWHousesUI.getLogger().info("World: " + house.getWorld());
        GTWHousesUI.getLogger().info("Kicked: " + house.isKicked());
        GTWHousesUI.getLogger().info("Rented at: " + house.getRentedAt());
        GTWHousesUI.getLogger().info("Rent due date: " + house.getRentDueDate());

        GuiPanel imagePanel = new GuiPanel();
        imagePanel.setCssClass("myHouseImage");
        // imagePanel.add(new GuiLabel("Image").setCssId("image"));

        HouseImage houseImage = HouseImagesManager.getImage(house);

        if (houseImage != null)
            imagePanel.getStyle().setTexture(new GuiTextureSprite(houseImage.getTexture(), 0, 0, houseImage.getWidth(), houseImage.getHeight(), houseImage.getWidth(), houseImage.getHeight()));

        add(imagePanel);

        GuiPanel contentPanel = new GuiPanel();
        contentPanel.setCssClass("houseContent");
        contentPanel.setLayout(GridLayout.columnLayout(50, 0));

        GuiPanel houseInfoPanel = new GuiPanel();
        houseInfoPanel.setCssClass("houseInfo");
        houseInfoPanel.setLayout(GridLayout.columnLayout(15, 2));

        String buyCost = String.format("%,d", house.getBuyCost());

        String rentValue = house.getRentCost() + "$/day";
        NetworkPlayerInfo playerInfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(house.getOwner());
        String ownerName = house.getOwner() == null ? "NO OWNER" : playerInfo == null ? "UNKNOWN" : playerInfo.getGameProfile().getName();

        String rentText = "Renting:";

        if (house.getRentDueDate() != null) {
            Date date = new Date();

            long diff = house.getRentDueDate().getTime() - date.getTime();
            boolean isDue = diff < 0;

            long absDiff = Math.abs(diff);
            long days = TimeUnit.MILLISECONDS.toDays(absDiff);
            long hours = TimeUnit.MILLISECONDS.toHours(absDiff) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(absDiff) % 60;

            if (house.isKicked()) {
                if (days > 0) {
                    rentText = "Kicking in " + days + " days";
                    if (hours > 0)
                        rentText += " and " + hours + " hours";
                } else if (hours > 0)
                    rentText = "Kicking in " + hours + " hours";
                else if (minutes > 0)
                    rentText = "Kicking in " + minutes + " minutes";
            } else if (days > 0)
                rentText = isDue ? "Rent overdue by " + days + " days" : "Rent due in " + days + " days";
            else if (hours > 0)
                rentText = isDue ? "Rent overdue by " + hours + " hours" : "Rent due in " + hours + " hours";
            else if (minutes > 0)
                rentText = isDue ? "Rent overdue by " + minutes + " minutes" : "Rent due in " + minutes + " minutes";
        }

        houseInfoPanel.add(new GuiLabel("Renting: " + house.getName()).setCssId("houseName"));
        houseInfoPanel.add(new GuiLabel("Buy Value: " + buyCost + "$").setCssId("housePrice"));
        houseInfoPanel.add(new GuiLabel("Owner: " + ownerName).setCssId("housePrice"));
        houseInfoPanel.add(new GuiLabel("Rent Value: " + rentValue).setCssId("houseLocation"));
        houseInfoPanel.add(new GuiLabel(rentText).setCssId("houseRent"));

        GuiPanel buttonsPanel = new GuiPanel();
        buttonsPanel.setLayout(new GridLayout(50, 15, 5, GridLayout.GridDirection.HORIZONTAL, 4));
        buttonsPanel.setCssClass("houseButtons");

        String rentAction = (house.isRented() && Minecraft.getMinecraft().player.getUniqueID().equals(house.getRenter())) ?
                "Stop Renting" : house.isKicked() ? "Already Kicked" : "Kick Tenant";

        GuiButton stopRentButton = new GuiButton(rentAction);
        stopRentButton.setCssId("stopRentButton").setCssClass("houseBtn");

        stopRentButton.addClickListener((pos, mouse, button) -> {
            HouseActions action = (house.isRented() && house.getRenter().equals(Minecraft.getMinecraft().player.getUniqueID())) ?
                    HouseActions.Unrent : HouseActions.Unrentable;

            if (house.isKicked() && action == HouseActions.Unrentable) return;

            add(new ConfirmModal(this, (x, y, pointer) -> {
                GTWNetworkHandler.sendToServer(new HouseActionC2SPacket(action, house.getName(), null));
                Minecraft.getMinecraft().displayGuiScreen(null);
            }));
        });

        buttonsPanel.add(stopRentButton);

        contentPanel.add(houseInfoPanel);
        contentPanel.add(buttonsPanel);

        add(contentPanel);
    }

    private static int numberOfDaysFromToday(Date date) {
        return (int) ((new Date().getTime() - date.getTime()) / (1000 * 60 * 60 * 24));
    }
}
