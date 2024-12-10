package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui;

import com.grandtheftwarzone.gtwhouses.client.gtwhouses.network.GTWNetworkHandler;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItem;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.SetItemPricePacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GuiItemPricing extends GuiScreen {
    private GuiTextField searchBar;
    private GuiTextField buyPriceField;
    private GuiTextField sellPriceField;

    private List<Item> allItems;
    private List<Item> filteredItems;
    private Item selectedItem = null;

    private int scrollOffset = 0;
    private static final int COLUMNS = 12;
    private static final int ICON_SIZE = 18;
    private int visibleRows;

    private HashMap<String, ShopItem> shopItems;

    public GuiItemPricing(HashMap<String, ShopItem> shopItems) {
        this.shopItems = shopItems;
    }

    @Override
    public void initGui() {
        allItems = new ArrayList<>(ForgeRegistries.ITEMS.getValuesCollection());
        filteredItems = new ArrayList<>(allItems);

        visibleRows = calculateVisibleRows();

        searchBar = new GuiTextField(0, fontRenderer, this.width / 2 - 100, 20, 200, 20);
        searchBar.setMaxStringLength(50);
        searchBar.setFocused(true);

        buyPriceField = new GuiTextField(1, fontRenderer, this.width / 2 - 80, this.height - 90, 70, 20);
        sellPriceField = new GuiTextField(2, fontRenderer, this.width / 2 + 10, this.height - 90, 70, 20);
        buyPriceField.setMaxStringLength(10);
        sellPriceField.setMaxStringLength(10);
        buyPriceField.setText("0");
        sellPriceField.setText("0");

        this.addButton(new GuiButton(0, this.width / 2 - 100, this.height - 40, 98, 20, "Set Prices"));
        this.addButton(new GuiButton(1, this.width / 2 + 2, this.height - 40, 98, 20, "Exit"));
    }

    private int calculateVisibleRows() {
        int startY = 50; // Start position of the grid
        int availableHeight = this.height - startY - 120; // Space for grid minus bottom elements
        return availableHeight / ICON_SIZE;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        searchBar.drawTextBox();
        drawCenteredString(fontRenderer, "Search Items", this.width / 2, 10, 0xFFFFFF);

        drawItemGrid(mouseX, mouseY);

        drawCenteredString(fontRenderer, "Buy Price:", this.width / 2 - 50, this.height - 105, 0xFFFFFF);
        drawCenteredString(fontRenderer, "Sell Price:", this.width / 2 + 40, this.height - 105, 0xFFFFFF);
        buyPriceField.drawTextBox();
        sellPriceField.drawTextBox();

        if (selectedItem != null) {
            String itemName = selectedItem.getRegistryName().toString();
            drawCenteredString(fontRenderer, "Selected Item: " + itemName, this.width / 2, this.height - 60, 0xFFFFFF); // Adjusted position
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawItemGrid(int mouseX, int mouseY) {
        int startX = this.width / 2 - (COLUMNS * ICON_SIZE) / 2;
        int startY = 50;

        List<String> hoverText = null;
        int hoverX = 0;
        int hoverY = 0;

        for (int row = 0; row < visibleRows; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int index = scrollOffset + row * COLUMNS + col;
                if (index >= filteredItems.size()) return;

                int x = startX + col * ICON_SIZE;
                int y = startY + row * ICON_SIZE;
                Item item = filteredItems.get(index);

                GlStateManager.enableBlend();
                itemRender.renderItemAndEffectIntoGUI(new ItemStack(item), x, y);
                itemRender.renderItemOverlayIntoGUI(fontRenderer, new ItemStack(item), x, y, null);
                GlStateManager.disableBlend();

                if (selectedItem != null && selectedItem.getRegistryName().equals(item.getRegistryName())) {
                    drawRect(x, y, x + ICON_SIZE, y + ICON_SIZE, 0x80FF0000);
                }

                // Check for hover
                if (mouseX >= x && mouseX <= x + ICON_SIZE && mouseY >= y && mouseY <= y + ICON_SIZE) {
                    drawRect(x, y, x + ICON_SIZE, y + ICON_SIZE, 0x80FFFFFF); // Light up on hover
                    hoverText = new ArrayList<>();
                    hoverText.add(item.getRegistryName().toString()); // Store tooltip text
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

        searchBar.mouseClicked(mouseX, mouseY, mouseButton);
        buyPriceField.mouseClicked(mouseX, mouseY, mouseButton);
        sellPriceField.mouseClicked(mouseX, mouseY, mouseButton);

        int startX = this.width / 2 - (COLUMNS * ICON_SIZE) / 2;
        int startY = 50;

        for (int row = 0; row < visibleRows; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int index = scrollOffset + row * COLUMNS + col;
                if (index >= filteredItems.size()) return;

                int x = startX + col * ICON_SIZE;
                int y = startY + row * ICON_SIZE;

                if (mouseX >= x && mouseX <= x + ICON_SIZE && mouseY >= y && mouseY <= y + ICON_SIZE) {
                    selectedItem = filteredItems.get(index);

                    ShopItem shopItem = shopItems.getOrDefault(selectedItem.getRegistryName().toString(), null);

                    if (shopItem != null) {
                        buyPriceField.setText(String.valueOf(shopItem.getBuyPrice()));
                        sellPriceField.setText(String.valueOf(shopItem.getSellPrice()));
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int delta = Mouse.getEventDWheel();
        if (delta != 0) {
            scrollOffset = Math.max(0, Math.min(scrollOffset - (delta > 0 ? 1 : -1) * COLUMNS, filteredItems.size() - visibleRows * COLUMNS));
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        // Update search filter
        if (searchBar.textboxKeyTyped(typedChar, keyCode)) {
            String query = searchBar.getText().toLowerCase();
            filteredItems = allItems.stream()
                    .filter(item -> item.getRegistryName().toString().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            scrollOffset = 0;
        }

        buyPriceField.textboxKeyTyped(typedChar, keyCode);
        sellPriceField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0 && selectedItem != null) {
            String buyPrice = buyPriceField.getText();
            String sellPrice = sellPriceField.getText();
            mc.player.sendMessage(new TextComponentString("Set prices for " + selectedItem.getRegistryName() + ": Buy " + buyPrice + ", Sell " + sellPrice));

            GTWNetworkHandler.sendToServer(new SetItemPricePacket(new ShopItem(selectedItem.getRegistryName().toString(), Integer.parseInt(buyPrice), 1, Integer.parseInt(sellPrice))));
        } else if (button.id == 1) {
            mc.displayGuiScreen(null);
        }
    }
}
