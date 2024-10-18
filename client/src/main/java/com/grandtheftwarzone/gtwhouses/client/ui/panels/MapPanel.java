package com.grandtheftwarzone.gtwhouses.client.ui.panels;

import com.grandtheftwarzone.gtwhouses.common.data.Marker;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.event.listeners.mouse.IMouseExtraClickListener;
import lombok.Getter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class MapPanel extends GuiPanel {
    public static final ResourceLocation MAP_LOCATION = new ResourceLocation("textures/gtwhouses_map.png");
    private static final ResourceLocation MARKER_LOCATION = new ResourceLocation("textures/mark.png");

    private final int MAP_WIDTH = 1018;
    private final int MAP_HEIGHT = 918;

    private final int MARKER_WIDTH = 23;
    private final int MARKER_HEIGHT = 35;

    private final int mapWorldX = -1372, mapWorldZ = -2636;
    private final int mapWorldEndX = 1994, mapWorldEndZ = 410;

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

    private String selectedHouse;
    private int hoveredMarkerIndex = -1;

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

                if (hoveredMarkerIndex != -1) {
                    Marker m = markers.get(hoveredMarkerIndex);
                    getParent().getChildComponents().forEach(c -> {
                        if (c instanceof GuiScrollPane) {
                            GuiScrollPane scrollPane = (GuiScrollPane) c;
                            scrollPane.getySlider().setValue(hoveredMarkerIndex * 200);

                            final int[] index = {0};
                            scrollPane.getChildComponents().forEach(h -> {
                                if (h instanceof HousePanel) {

                                    h.setCssClass("housePanel");

                                    if (index[0] == hoveredMarkerIndex)
                                        h.setCssClass("selectedHousePanel");

                                    index[0]++;
                                }

                            });
                        }
                    });

                    centerMap(m.getX(), m.getZ());

                    selectedHouse = m.getHouseName();
                    hoveredMarkerIndex = -1;
                }
            }
        });

        addWheelListener(i -> {
            int[] centerWorldPos = getCenterWorldPos();
            int mapViewCenterWorldX = centerWorldPos[0];
            int mapViewCenterWorldY = centerWorldPos[1];

            int prevZoom = (int) zoom;

            zoom += 0.0004f * i;
            zoom = Math.max(0.4f, zoom);
            zoom = Math.min(1.9f, zoom);

            int offset = prevZoom < zoom ? 2 : -2;

            centerMap(mapViewCenterWorldX + offset, mapViewCenterWorldY);
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

        float mouseXOnMap = (mouseX - getScreenX() + offsetX) / zoom;
        float mouseYOnMap = (mouseY - getScreenY() + offsetY) / zoom;

        hoveredMarkerIndex = -1;

        for (int i = 0; i < markers.size(); i++) {
            Marker marker = markers.get(i);

            int markerMapWorldX = marker.getX() - mapWorldX;
            int markerMapWorldY = marker.getZ() - mapWorldZ;

            int markerScreenX = (int) (markerMapWorldX * worldToMapScaleX) - MARKER_WIDTH / 2; // Center of the marker
            int markerScreenY = (int) (markerMapWorldY * worldToMapScaleY) - MARKER_HEIGHT; // Bottom of the marker


            if (mouseXOnMap >= markerScreenX && mouseXOnMap <= markerScreenX + MARKER_WIDTH && mouseYOnMap >= markerScreenY && mouseYOnMap <= markerScreenY + MARKER_HEIGHT)
                hoveredMarkerIndex = i;

            if (marker.getHouseName().equals(selectedHouse))
                GlStateManager.color(1, 0.9f, 0, 1);
            else if (hoveredMarkerIndex == i)
                GlStateManager.color(1f, 0f, 0.2f, 1);
            else
                GlStateManager.color(1, 1, 1, 1);

            Gui.drawModalRectWithCustomSizedTexture(markerScreenX, markerScreenY, 0, 0, MARKER_WIDTH, MARKER_HEIGHT, MARKER_WIDTH, MARKER_HEIGHT);
        }
        GlStateManager.popMatrix();

        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    public void selectMarker(Marker marker) {
        selectedHouse = marker.getHouseName();
        centerMap(marker.getX(), marker.getZ());
    }

    private void centerMap(int worldX, int worldZ) {
        float mapX = ((worldX - mapWorldX) * worldToMapScaleX) * zoom;
        float mapY = ((worldZ - mapWorldZ) * worldToMapScaleY) * zoom;

        offsetX = (int) ((mapX - getWidth() / 2));
        offsetY = (int) ((mapY - getHeight() / 2));

        clampOffset();
    }

    public int[] getCenterWorldPos() {
        float viewCenterX = getWidth() / 2f;
        float viewCenterY = getHeight() / 2f;

        float mapCenterX = (viewCenterX + offsetX) / zoom;
        float mapCenterY = (viewCenterY + offsetY) / zoom;

        int worldX = (int) (mapWorldX + mapCenterX / worldToMapScaleX);
        int worldZ = (int) (mapWorldZ + mapCenterY / worldToMapScaleY);

        return new int[]{worldX, worldZ};
    }


    private void clampOffset() {
        float zoomedMapWidth = (MAP_WIDTH * zoom);
        float zoomedMapHeight = (MAP_HEIGHT * zoom);

        float maxOffsetX = zoomedMapWidth - getWidth();
        float maxOffsetY = zoomedMapHeight - getHeight();

        offsetX = Math.max(0, Math.min(offsetX, (int) maxOffsetX));
        offsetY = Math.max(0, Math.min(offsetY, (int) maxOffsetY));
    }
}