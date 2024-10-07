package com.grandtheftwarzone.gtwhouses.client.ui.panels.map;

import com.grandtheftwarzone.gtwhouses.client.ui.panels.map.MapMarker;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.event.listeners.mouse.IMouseExtraClickListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseWheelListener;
import fr.aym.acsguis.utils.GuiTextureSprite;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class MapPanel extends GuiPanel {
    public static final ResourceLocation MAP_LOCATION = new ResourceLocation("textures/map2.png");
    private final int MAP_WIDTH = 1919;
    private final int MAP_HEIGHT = 974;

    private final int mapWorldX = -2226;
    private final int mapWorldZ = -2316;

    int mapX = 0;
    int mapY = 0;

    private float zoom = 1;
    private int lastMouseX;
    private int lastMouseY;
    private boolean dragging = false;

    private GuiTextureSprite mapSprite;

    public MapPanel() {
        super();

        mapSprite = new GuiTextureSprite(MAP_LOCATION, 0, 0, MAP_WIDTH, MAP_HEIGHT);
        addExtraClickListener(new IMouseExtraClickListener() {
            @Override
            public void onMouseDoubleClicked(int i, int i1, int i2) {
                centerMap(3080 - mapWorldX, 366 - mapWorldZ);
            }

            @Override
            public void onMousePressed(int i, int i1, int i2) {
                dragging = true;
                lastMouseX = i;
                lastMouseY = i1;
            }

            @Override
            public void onMouseReleased(int i, int i1, int i2) {
                dragging = false;
            }
        });

        addWheelListener(i -> {
            zoom += 0.0005f * i;
            zoom = Math.max(0.35f, Math.min(1.25f, zoom));
        });
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTicks) {
        int mapWidthToPanelOffset = (int) (MAP_WIDTH * zoom - getWidth()) / 2;
        int mapHeightToPanelOffset = (int) (MAP_HEIGHT * zoom - getHeight()) / 2;

        if (dragging) {
            mapX += lastMouseX - mouseX;
            mapY += lastMouseY - mouseY;

            mapX = Math.min(mapWidthToPanelOffset, Math.max(-mapWidthToPanelOffset, mapX));
            mapY = Math.min(mapHeightToPanelOffset, Math.max(-mapHeightToPanelOffset, mapY));
        }

        int panelCenterX = (int) (getScreenX() + getWidth() / 2);
        int panelCenterY = (int) (getScreenY() + getHeight() / 2);

        int mapCenterX = mapX + (int) (MAP_WIDTH * zoom / 2);
        int mapCenterY = mapY + (int) (MAP_HEIGHT * zoom / 2);

        int mapX = panelCenterX - mapCenterX;
        int mapY = panelCenterY - mapCenterY;

        GlStateManager.pushMatrix();
        GlStateManager.translate(mapX, mapY, 0);
        GlStateManager.scale(zoom, zoom, 1);
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(MAP_LOCATION);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, MAP_WIDTH, MAP_HEIGHT, MAP_WIDTH, MAP_HEIGHT);
        GlStateManager.popMatrix();


        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    public void centerMap(int worldX, int worldY) {
        int mapX = (int) ((worldX - mapWorldX) * zoom);
        int mapY = (int) ((worldY - mapWorldZ) * zoom);

        this.mapX = (int) (-mapX + getWidth() / 2);
        this.mapY = (int) (-mapY + getHeight() / 2);
    }
}