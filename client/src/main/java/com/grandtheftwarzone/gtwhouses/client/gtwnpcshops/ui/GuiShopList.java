package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui;

import com.grandtheftwarzone.gtwhouses.client.gtwhouses.network.GTWNetworkHandler;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.AdminShopGUI;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.Shop;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.DeleteShopPacket;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.OpenAdminShopGuiPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GuiShopList extends GuiScreen {
    private GuiTextField shopSearchBar;
    private List<Shop> shopList;
    private List<Shop> filteredShopList;

    private HashMap<String, GuiButton> deleteButtons = new HashMap<>();
    private HashMap<String, GuiButton> editButtons = new HashMap<>();

    private int scrollOffset = 0;
    private int maxScroll;

    public GuiShopList(Collection<Shop> shops) {
        this.shopList = new ArrayList<>(shops);
        this.filteredShopList = new ArrayList<>(shopList);
    }

    @Override
    public void initGui() {
        shopSearchBar = new GuiTextField(4, fontRenderer, this.width / 2 - 100, 50, 200, 20);
        shopSearchBar.setMaxStringLength(50);
        shopSearchBar.setFocused(false);

        this.buttonList.add(new GuiButton(2, this.width - 110, 10, 100, 20, "Shop Creation"));
        this.buttonList.add(new GuiButton(3, this.width - 110, 40, 100, 20, "Item Pricing"));

        maxScroll = Math.max(0, filteredShopList.size() * 30 - (this.height - 20));

        editButtons.clear();
        deleteButtons.clear();

        for (Shop s : shopList) {
            editButtons.put(s.getName(), this.addButton(new GuiButton(5, this.width / 2 + 10, 80, 40, 20, "Edit")));
            deleteButtons.put(s.getName(), this.addButton(new GuiButton(6, this.width / 2 + 60, 80, 40, 20, "Delete")));
        }

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.mc.world == null) return;

        this.drawDefaultBackground();

        shopSearchBar.drawTextBox();
        drawCenteredString(fontRenderer, "Search Shops", this.width / 2, 40, 0xFFFFFF);

        int yPosition = 80 - scrollOffset;
        for (Shop shop : filteredShopList) {
            GuiButton deleteButton = this.deleteButtons.get(shop.getName());
            GuiButton editButton = this.editButtons.get(shop.getName());
            if (yPosition >= 80 && yPosition < this.height - 20) {
                drawString(fontRenderer, shop.getName(), this.width / 2 - 100, yPosition, 0xFFFFFF);

                if (editButton != null) {
                    editButton.visible = true;
                    editButton.x = this.width / 2 + 10;
                    editButton.y = yPosition - 5;
                  //  editButton.drawButton(mc, mouseX, mouseY, partialTicks);
                }

                if (deleteButton != null) {
                    deleteButton.visible = true;
                    deleteButton.x = this.width / 2 + 60;
                    deleteButton.y = yPosition - 5;
                   // deleteButton.drawButton(mc, mouseX, mouseY, partialTicks);
                }
            } else {
                if (editButton != null) {
                    editButton.visible = false;
                }

                if (deleteButton != null) {
                    deleteButton.visible = false;
                }
            }
            yPosition += 30;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        shopSearchBar.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (shopSearchBar.textboxKeyTyped(typedChar, keyCode)) {
            String query = shopSearchBar.getText().toLowerCase();
            filteredShopList = shopList.stream()
                    .filter(shop -> shop.getName().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 2) {
            GTWNetworkHandler.sendToServer(new OpenAdminShopGuiPacket(AdminShopGUI.SHOP_CREATION, null, null));
        } else if (button.id == 3) {
            GTWNetworkHandler.sendToServer(new OpenAdminShopGuiPacket(AdminShopGUI.ITEMS_PRICING, null, null));
        } else if (button.id == 5) {
            // Edit shop logic
        } else if (button.id == 6) {
            for (Map.Entry<String, GuiButton> entry : deleteButtons.entrySet()) {
                if (entry.getValue() == button) {
                    GTWNetworkHandler.sendToServer(new DeleteShopPacket(entry.getKey()));
                    break;
                }
            }

            GTWNetworkHandler.sendToServer(new OpenAdminShopGuiPacket(AdminShopGUI.SHOP_LIST, null, null));
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        if (scroll != 0) {
            scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset + scroll / 120 * 30));
        }
    }
}