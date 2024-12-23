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
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GuiSell extends GuiContainer {
    private static final ResourceLocation sellGuiTextures = new ResourceLocation("textures/gui/sell.png");
    private final InventoryPlayer playerInventory;
    private final ContainerSell containerSell;
    private GuiButton sellButton;

    public GuiSell(List<ShopItem> items) {
        super(new ContainerSell(Minecraft.getMinecraft().player.inventory, items));
        this.playerInventory = Minecraft.getMinecraft().player.inventory;
        this.containerSell = (ContainerSell) this.inventorySlots;
        this.xSize = 256;
        this.ySize = 256;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.sellButton = new GuiButton(0, guiLeft + 180, guiTop + 4, 60, 20, "Sell");
        this.buttonList.add(this.sellButton);
        Mouse.setGrabbed(false);
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

            if (containerSell.getProfit() <= 0) return;
            GTWNetworkHandler.sendToServer(new SellItemsPacket(containerSell.getItemsToSell()));

            Minecraft.getMinecraft().player.closeScreen();
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (this.mc == null) return;
        this.fontRenderer.drawString("Sell Items", 8, 10, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, 137, 4210752);
        this.fontRenderer.drawString("Profit: ", 103, 10, 4210752);
        this.fontRenderer.drawString(containerSell.getProfit() + "$", 135, 10, 0x1ec71e);


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
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
    }
}