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

import java.util.Collection;

public class GuiShop extends GuiContainer {
    private static final ResourceLocation SHOP_GUI_TEXTURE = new ResourceLocation("textures/gui/shop.png");
    private final ContainerShop containerShop;

    public GuiShop(Shop shop, Collection<ShopItem> items) {
        super(new ContainerShop(shop, items));
        this.containerShop = (ContainerShop) inventorySlots;

        org.lwjgl.input.Mouse.setGrabbed(false);
    }

    @Override
    public void initGui() {

        this.xSize = (int) (432 / 1.685); //For some random reason pixels dont match the texture
        this.ySize = (int) (474 / 1.85);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        GuiButton buyButton = new GuiButton(1, x + 10, y + 50, 60, 20, "Buy");
        this.buttonList.add(buyButton);

        super.initGui();
        org.lwjgl.input.Mouse.setGrabbed(false);
        mc.mouseHelper.ungrabMouseCursor();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.FONT_HEIGHT = 16;
        this.fontRenderer.drawString("Min Level: 1", 12, 100, 4210752);
        this.fontRenderer.drawString(String.valueOf(containerShop.getTotalCost()), 28, 117, 0x1ec71e);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (this.mc == null) return;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(SHOP_GUI_TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        //org.lwjgl.input.Mouse.setGrabbed(false);
        this.renderHoveredToolTip(mouseX, mouseY);
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