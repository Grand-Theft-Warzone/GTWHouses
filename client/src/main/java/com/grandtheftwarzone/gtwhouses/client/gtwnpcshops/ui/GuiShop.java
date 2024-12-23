package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui;

import com.grandtheftwarzone.gtwhouses.client.gtwhouses.network.GTWNetworkHandler;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.Shop;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItem;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.packets.BuyItemsPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Mouse;

import java.util.Collection;
import java.util.stream.Collectors;

public class GuiShop extends GuiContainer {
    private static final ResourceLocation SHOP_GUI_TEXTURE = new ResourceLocation("textures/gui/shop.png");
    private final ContainerShop containerShop;

    private final Shop shop;

    public GuiShop(Shop shop, Collection<ShopItem> items) {
        super(new ContainerShop(shop, items));
        this.shop = shop;
        this.containerShop = (ContainerShop) inventorySlots;
    }

    @Override
    public void initGui() {
        this.xSize = 256;
        this.ySize = 256;

        super.initGui();
        this.addButton(new GuiButton(1, guiLeft + 180, guiTop + 197, 60, 20, "Buy"));
        Mouse.setGrabbed(false);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        //this.fontRenderer.FONT_HEIGHT = 16;
        this.fontRenderer.drawString(shop.getName(), 8, 6, 4210752);
        this.fontRenderer.drawString("Buy List", 8, 129, 4210752);
        this.fontRenderer.drawString("Cost: ", 12, 202, 4210752);
        this.fontRenderer.drawString(containerShop.getTotalCost() + "$", 45, 202, 0x1ec71e);

        this.fontRenderer.drawString("Level: ", 97, 202, 4210752);
        this.fontRenderer.drawString(containerShop.getMinLevel() + "", 135, 202,  0xff0000);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (this.mc == null) return;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(SHOP_GUI_TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.mc == null) return;
        super.drawScreen(mouseX, mouseY, partialTicks);
        //org.lwjgl.input.Mouse.setGrabbed(false);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Mouse.setGrabbed(true);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            if (containerShop.getTotalCost() > 0) {
                GTWNetworkHandler.sendToServer(new BuyItemsPacket(containerShop.getBuyList()));
                mc.player.closeScreen();
            }
        }
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        //super.handleMouseClick(slotIn, slotId, mouseButton, type);
        containerShop.customSlotClip(slotId, mouseButton, type, mc.player);
    }

}