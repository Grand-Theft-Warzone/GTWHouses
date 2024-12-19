package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui;

import com.grandtheftwarzone.gtwhouses.client.gtwhouses.network.GTWNetworkHandler;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItem;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.SellItemsPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Map;

public class GuiSell extends GuiContainer {
    private static final ResourceLocation sellGuiTextures = new ResourceLocation("gtwhouses", "textures/gui/sell_gui.png");
    private final InventoryPlayer playerInventory;
    private final ContainerSell containerSell;
    private GuiButton sellButton;

    public GuiSell(Map<String, ShopItem> items) {
        super(new ContainerSell(Minecraft.getMinecraft().player.inventory, items));
        this.playerInventory = Minecraft.getMinecraft().player.inventory;
        this.containerSell = (ContainerSell) this.inventorySlots;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void initGui() {
        super.initGui();
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.sellButton = new GuiButton(0, k + 10, l + 70, 60, 20, "Sell");
        this.buttonList.add(this.sellButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.mc == null) return;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            containerSell.calculateProfit();

            Map<String, Integer> items = containerSell.getItemsToSell();
            if (items.size() > 0)
                GTWNetworkHandler.sendToServer(new SellItemsPacket(items));

            Minecraft.getMinecraft().player.closeScreen();
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (this.mc == null) return;
        this.fontRenderer.drawString("Sell Items", 8, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
        this.fontRenderer.drawString("Profit: " + containerSell.getProfit(), 8, this.ySize - 96 + 12, 4210752);

        // Gray out slots that are not sellable
        for (Slot slot : this.inventorySlots.inventorySlots) {
            ItemStack stack = slot.getStack();
            if (!stack.isEmpty() && !containerSell.isSellable(stack)) {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.colorMask(true, true, true, false);
                this.drawGradientRect(slot.xPos, slot.yPos, slot.xPos + 16, slot.yPos + 16, 0x80FF0000, 0x80FF0000);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        containerSell.mySlotClicked(slotIn, slotId, mouseButton, type);
        containerSell.calculateProfit();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (this.mc == null) return;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(sellGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }
}