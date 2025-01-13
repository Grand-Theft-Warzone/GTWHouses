package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui;

import com.grandtheftwarzone.gtwhouses.client.gtwhouses.network.GTWNetworkHandler;
import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ItemUtils;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.AdminShopGUI;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.Shop;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItem;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.CreateShopPacket;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.OpenAdminShopGuiPacket;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.UpdateShopPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GuiShopEdit extends GuiScreen {
    private GuiTextField shopNameField;
    private GuiTextField searchField;
    private List<ItemStack> allItems;
    private List<ItemStack> filteredItems;
    private List<ItemStack> selectedItems;
    private int scrollOffset = 0;
    private static final int COLUMNS = 12;
    private static final int ICON_SIZE = 18;
    private int visibleRows;

    private List<ShopItem> shopItems;
    private Shop originalShop;
    private HashMap<String, Shop> shops;
    private GuiButton editButton;

    public GuiShopEdit(Shop shop, HashMap<String, Shop> shops, List<ShopItem> shopItems) {
        this.originalShop = shop;
        this.shopItems = shopItems;
        this.shops = shops;
    }

    @Override
    public void initGui() {
        allItems = shopItems.stream().map((i) -> ItemUtils.deserializeItemStack(i.getSerializedItemStack())).collect(Collectors.toList());
        // allItems = new ArrayList<>(ForgeRegistries.ITEMS.getValuesCollection());
        filteredItems = new ArrayList<>(allItems);

        selectedItems = allItems.stream()
                .filter(item -> originalShop.getItems().contains(ItemUtils.serializeItemStack(item)))
                .collect(Collectors.toList());

        visibleRows = calculateVisibleRows();

        shopNameField = new GuiTextField(0, fontRenderer, this.width / 2 - 100, 20, 200, 20);
        shopNameField.setMaxStringLength(50);
        shopNameField.setFocused(true);
        shopNameField.setText(originalShop.getName());

        searchField = new GuiTextField(1, fontRenderer, this.width / 2 - 100, 60, 200, 20);
        searchField.setMaxStringLength(50);
        editButton = new GuiButton(0, this.width / 2 - 100, this.height - 40, 98, 20, "Edit Shop");
        this.addButton(editButton);
        this.addButton(new GuiButton(1, this.width / 2 + 2, this.height - 40, 98, 20, "Exit"));

        this.buttonList.add(new GuiButton(2, this.width - 110, 10, 100, 20, "Shop List"));
        this.buttonList.add(new GuiButton(3, this.width - 110, 40, 100, 20, "Item Pricing"));
        Mouse.setGrabbed(false);
    }

    private int calculateVisibleRows() {
        int startY = 90; // Start position of the grid
        int availableHeight = this.height - startY - 60; // Space for grid minus bottom elements
        return availableHeight / ICON_SIZE;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.mc == null) return;
        this.drawDefaultBackground();

        if (shopNameField != null) {
            shopNameField.drawTextBox();
            drawCenteredString(fontRenderer, "Shop Name", this.width / 2, 10, 0xFFFFFF);
        }

        if (searchField != null) {
            searchField.drawTextBox();
            drawCenteredString(fontRenderer, "Search Items", this.width / 2, 50, 0xFFFFFF);
        }

        if (filteredItems != null) {
            drawItemGrid(mouseX, mouseY, filteredItems, 90);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawItemGrid(int mouseX, int mouseY, List<ItemStack> items, int startY) {
        int startX = this.width / 2 - (COLUMNS * ICON_SIZE) / 2;

        List<String> hoverText = null;
        int hoverX = 0;
        int hoverY = 0;

        for (int row = 0; row < visibleRows; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int index = scrollOffset + row * COLUMNS + col;
                if (index >= items.size()) return;

                int x = startX + col * ICON_SIZE;
                int y = startY + row * ICON_SIZE;
                ItemStack item = items.get(index);

                GlStateManager.enableBlend();
                itemRender.renderItemAndEffectIntoGUI(item, x, y);
                itemRender.renderItemOverlayIntoGUI(fontRenderer, item, x, y, null);
                GlStateManager.disableBlend();

                if (selectedItems.contains(item)) {
                    drawRect(x, y, x + ICON_SIZE, y + ICON_SIZE, 0x80FF0000); // Highlight selected items
                }

                if (mouseX >= x && mouseX <= x + ICON_SIZE && mouseY >= y && mouseY <= y + ICON_SIZE) {
                    drawRect(x, y, x + ICON_SIZE, y + ICON_SIZE, 0x80FFFFFF); // Light up on hover
                    hoverText = new ArrayList<>();
                    hoverText.add(item.getDisplayName()); // Store tooltip text
                    hoverX = mouseX;
                    hoverY = mouseY;
                }
            }
        }

        if (hoverText != null) {
            drawHoveringText(hoverText, hoverX, hoverY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        shopNameField.mouseClicked(mouseX, mouseY, mouseButton);
        searchField.mouseClicked(mouseX, mouseY, mouseButton);

        int startX = this.width / 2 - (COLUMNS * ICON_SIZE) / 2;
        int startY = 90;

        for (int row = 0; row < visibleRows; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int index = scrollOffset + row * COLUMNS + col;
                if (index >= filteredItems.size()) return;

                int x = startX + col * ICON_SIZE;
                int y = startY + row * ICON_SIZE;

                if (mouseX >= x && mouseX <= x + ICON_SIZE && mouseY >= y && mouseY <= y + ICON_SIZE) {
                    ItemStack item = filteredItems.get(index);
                    if (!selectedItems.contains(item)) {
                        selectedItems.add(item);
                    } else {
                        selectedItems.remove(item);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scrollDelta = Integer.signum(Mouse.getEventDWheel());
        scrollOffset = Math.max(0, Math.min(scrollOffset - scrollDelta * COLUMNS, filteredItems.size() - visibleRows * COLUMNS));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        searchField.textboxKeyTyped(typedChar, keyCode);
        shopNameField.textboxKeyTyped(typedChar, keyCode);
        updateFilteredItems();
    }

    private void updateFilteredItems() {
        String query = searchField.getText().toLowerCase();
        filteredItems = allItems.stream()
                .filter(item -> item.getItem().getRegistryName().toString().toLowerCase().contains(query))
                .collect(Collectors.toList());

        String shopName = shopNameField.getText();
        editButton.enabled = !shopName.isEmpty();

        if (!shopName.equalsIgnoreCase(originalShop.getName())&& shops.containsKey(shopName)) {
            editButton.enabled = false;
            editButton.displayString = "Shop Name Taken";
        } else {
            editButton.displayString = "Update Shop";
        }

    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            String shopName = shopNameField.getText();
            if (shopName.isEmpty()) return;

            Shop shop = new Shop(shopName, selectedItems.stream().map(ItemUtils::serializeItemStack).collect(Collectors.toList()));
            GTWNetworkHandler.sendToServer(new UpdateShopPacket(originalShop.getName(), shop));
            mc.displayGuiScreen(null);
        } else if (button.id == 1) {
            mc.displayGuiScreen(null);
        } else if (button.id == 2) {
            GTWNetworkHandler.sendToServer(new OpenAdminShopGuiPacket(AdminShopGUI.SHOP_LIST, null, null));
        } else if (button.id == 3) {
            GTWNetworkHandler.sendToServer(new OpenAdminShopGuiPacket(AdminShopGUI.ITEMS_PRICING, null, null));
        }
    }
}