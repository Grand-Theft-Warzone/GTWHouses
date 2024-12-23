package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui;

import com.grandtheftwarzone.gtwhouses.client.gtwhouses.network.GTWNetworkHandler;
import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ItemUtils;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.AdminShopGUI;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItem;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.OpenAdminShopGuiPacket;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.SetItemPricePacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
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
    private GuiTextField levelRequirementField;

    private List<ItemStack> allItems = new ArrayList<>();
    private List<ItemStack> filteredItems = new ArrayList<>();
    private ItemStack selectedItem = ItemStack.EMPTY;

    private int scrollOffset = 0;
    private static final int COLUMNS = 12;
    private static final int ICON_SIZE = 18;
    private int visibleRows;

    private final List<ShopItem> shopItems;

    public GuiItemPricing(List<ShopItem> shopItems) {
        this.shopItems = shopItems;
    }

    @Override
    public void initGui() {
        visibleRows = calculateVisibleRows();

        searchBar = new GuiTextField(0, fontRenderer, this.width / 2 - 100, 20, 200, 20);
        searchBar.setMaxStringLength(50);
        searchBar.setFocused(true);

        buyPriceField = new GuiTextField(1, fontRenderer, this.width / 2 - 120, this.height - 90, 70, 20);
        sellPriceField = new GuiTextField(2, fontRenderer, this.width / 2 - 40, this.height - 90, 70, 20);
        levelRequirementField = new GuiTextField(3, fontRenderer, this.width / 2 + 40, this.height - 90, 70, 20);
        buyPriceField.setMaxStringLength(10);
        sellPriceField.setMaxStringLength(10);
        levelRequirementField.setMaxStringLength(10);
        buyPriceField.setText("0");
        sellPriceField.setText("0");
        levelRequirementField.setText("0");

        this.addButton(new GuiButton(0, this.width / 2 - 100, this.height - 40, 98, 20, "Set Prices"));
        this.addButton(new GuiButton(1, this.width / 2 + 2, this.height - 40, 98, 20, "Exit"));
        this.addButton(new GuiButton(2, this.width - 110, 10, 100, 20, "Shop List"));
        this.addButton(new GuiButton(3, this.width - 110, 40, 100, 20, "Shop Creation"));

        List<Item> items = new ArrayList<>(ForgeRegistries.ITEMS.getValuesCollection());

        for (Item item : items) {
            NonNullList<ItemStack> subItems = NonNullList.create();
            item.getSubItems(CreativeTabs.SEARCH, subItems);
            allItems.addAll(subItems);
        }
        filteredItems = new ArrayList<>(allItems);

        Mouse.setGrabbed(false);
    }

    private int calculateVisibleRows() {
        int startY = 50; // Start position of the grid
        int availableHeight = this.height - startY - 120; // Space for grid minus bottom elements
        return availableHeight / ICON_SIZE;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.mc == null) return;
        this.drawDefaultBackground();

        searchBar.drawTextBox();
        drawCenteredString(fontRenderer, "Search Items", this.width / 2, 10, 0xFFFFFF);

        drawItemGrid(mouseX, mouseY);

        drawCenteredString(fontRenderer, "Buy Price:", this.width / 2 - 90, this.height - 105, 0xFFFFFF);
        drawCenteredString(fontRenderer, "Sell Price:", this.width / 2 - 10, this.height - 105, 0xFFFFFF);
        drawCenteredString(fontRenderer, "Min Level:", this.width / 2 + 70, this.height - 105, 0xFFFFFF);
        buyPriceField.drawTextBox();
        sellPriceField.drawTextBox();
        levelRequirementField.drawTextBox();

        if (selectedItem != null) {
            String itemName = selectedItem.getItem().getRegistryName().toString();
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
                ItemStack item = filteredItems.get(index);

                GlStateManager.enableBlend();
                itemRender.renderItemAndEffectIntoGUI(item, x, y);
                itemRender.renderItemOverlayIntoGUI(fontRenderer, item, x, y, null);
                GlStateManager.disableBlend();

                if (selectedItem != null && selectedItem.getItem() == item.getItem() && selectedItem.getMetadata() == item.getMetadata()) {
                    drawRect(x, y, x + ICON_SIZE, y + ICON_SIZE, 0x80FF0000);
                }


                // Check for hover
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

        searchBar.mouseClicked(mouseX, mouseY, mouseButton);
        buyPriceField.mouseClicked(mouseX, mouseY, mouseButton);
        sellPriceField.mouseClicked(mouseX, mouseY, mouseButton);
        levelRequirementField.mouseClicked(mouseX, mouseY, mouseButton);

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

                    ShopItem shopItem = shopItems.stream()
                            .filter(item -> ItemUtils.serializeItemStack(selectedItem).equalsIgnoreCase(item.getSerializedItemStack()))
                            .findFirst().orElse(null);

                    if (shopItem != null) {
                        buyPriceField.setText(String.valueOf(shopItem.getBuyPrice()));
                        sellPriceField.setText(String.valueOf(shopItem.getSellPrice()));
                        levelRequirementField.setText(String.valueOf(shopItem.getBuyLevel()));
                    } else {
                        buyPriceField.setText("0");
                        sellPriceField.setText("0");
                        levelRequirementField.setText("0");
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

        if (searchBar.textboxKeyTyped(typedChar, keyCode)) {
            String query = searchBar.getText().toLowerCase();
            filteredItems = allItems.stream()
                    .filter(item -> item.getItem().getRegistryName().toString().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            scrollOffset = 0;
        }

        buyPriceField.textboxKeyTyped(typedChar, keyCode);
        sellPriceField.textboxKeyTyped(typedChar, keyCode);
        levelRequirementField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0 && selectedItem != null) {
            String buyPrice = buyPriceField.getText();
            String sellPrice = sellPriceField.getText();
            String levelRequirement = levelRequirementField.getText();
            mc.player.sendMessage(new TextComponentString("Set prices for " + selectedItem.getItem().getRegistryName() + ": Buy " + buyPrice + ", Sell " + sellPrice));

            GTWNetworkHandler.sendToServer(new SetItemPricePacket(new ShopItem(ItemUtils.serializeItemStack(selectedItem), Integer.parseInt(buyPrice), Integer.parseInt(levelRequirement), Integer.parseInt(sellPrice))));
        } else if (button.id == 1) {
            mc.displayGuiScreen(null);
        } else if (button.id == 2) {
            GTWNetworkHandler.sendToServer(new OpenAdminShopGuiPacket(AdminShopGUI.SHOP_LIST, null, null));
        } else if (button.id == 3) {
            GTWNetworkHandler.sendToServer(new OpenAdminShopGuiPacket(AdminShopGUI.SHOP_CREATION, null, null));
        }
    }
}
