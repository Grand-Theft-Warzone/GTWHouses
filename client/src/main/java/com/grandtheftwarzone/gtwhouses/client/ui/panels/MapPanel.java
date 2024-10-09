package com.grandtheftwarzone.gtwhouses.client.ui.panels;

import com.grandtheftwarzone.gtwhouses.common.data.Marker;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.event.listeners.mouse.IMouseExtraClickListener;
import lombok.Getter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class MapPanel extends GuiPanel {
    public static final ResourceLocation MAP_LOCATION = new ResourceLocation("textures/gtwhouses_map.png");
    private static final ResourceLocation MARKER_LOCATION = new ResourceLocation("textures/mark.png");

    private final int MAP_WIDTH = 1919;
    private final int MAP_HEIGHT = 974;

    private final int MARKER_WIDTH = 23;
    private final int MARKER_HEIGHT = 35;

    private final int mapWorldX = -2226, mapWorldZ = -2316;
    private final int mapWorldEndX = 3080, mapWorldEndZ = 366;

    private final int worldWidth = mapWorldEndX - mapWorldX, worldHeight = mapWorldEndZ - mapWorldZ;

    private final float worldToMapScaleX = (float) MAP_WIDTH / (float) worldWidth;
    private final float worldToMapScaleY = (float) MAP_HEIGHT / (float) worldHeight;

    private List<Marker> markers;

    @Getter
    int offsetX = 0;
    @Getter
    int offsetY = 0;

    @Getter
    private float zoom = 1;

    private int lastMouseX;
    private int lastMouseY;
    private boolean dragging = false;

    public MapPanel(List<Marker> markers, int offsetX, int offsetY, float startZoom) {
        super();
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.zoom = startZoom;
        this.markers = markers;

        if (startZoom == 1 && offsetX == 0 && offsetY == 0)
            centerMap(mapWorldX + worldWidth / 2, mapWorldZ + worldHeight / 2);

        addExtraClickListener(new IMouseExtraClickListener() {
            @Override
            public void onMouseDoubleClicked(int i, int i1, int i2) {
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
            zoom += 0.0004f * i;
            zoom = Math.max(0.25f, zoom);
            zoom = Math.min(2, zoom);

            clampOffset();
        });
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTicks) {
        if (dragging) {
            offsetX += lastMouseX - mouseX;
            offsetY += lastMouseY - mouseY;
            clampOffset();
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(getScreenX(), getScreenY(), 0);
        GlStateManager.translate(-offsetX, -offsetY, 0);
        GlStateManager.scale(zoom, zoom, 1);
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(MAP_LOCATION);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, MAP_WIDTH, MAP_HEIGHT, MAP_WIDTH, MAP_HEIGHT);

        mc.getTextureManager().bindTexture(MARKER_LOCATION);

        for (Marker marker : markers) {
            int markerMapWorldX = marker.getX() - mapWorldX;
            int markerMapWorldY = marker.getZ() - mapWorldZ;

            int markerScreenX = (int) (markerMapWorldX * worldToMapScaleX) - MARKER_WIDTH / 2; //Center of the marker
            int markerScreenY = (int) (markerMapWorldY * worldToMapScaleY) - MARKER_HEIGHT; //Bottom of the marker


            Gui.drawModalRectWithCustomSizedTexture(markerScreenX, markerScreenY, 0, 0, MARKER_WIDTH, MARKER_HEIGHT, MARKER_WIDTH, MARKER_HEIGHT);
        }
        GlStateManager.popMatrix();


        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    public void centerMap(int worldX, int worldZ) {
        float mapX = ((worldX - mapWorldX) * worldToMapScaleX) * zoom;
        float mapY = ((worldZ - mapWorldZ) * worldToMapScaleY) * zoom;

        offsetX = (int) ((mapX - getWidth() / 2));
        offsetY = (int) ((mapY - getHeight() / 2));

        clampOffset();
    }


    private void clampOffset() {
        int zoomedMapWidth = (int) (MAP_WIDTH * zoom);
        int zoomedMapHeight = (int) (MAP_HEIGHT * zoom);

        int maxOffsetX = zoomedMapWidth - (int) getWidth();
        int maxOffsetY = zoomedMapHeight - (int) getHeight();

        offsetX = Math.max(0, Math.min(offsetX, maxOffsetX));
        offsetY = Math.max(0, Math.min(offsetY, maxOffsetY));
    }
}