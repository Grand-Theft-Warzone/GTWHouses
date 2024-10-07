package com.grandtheftwarzone.gtwhouses.client.ui.panels.map;

public class MapMarker {
    private final float x;
    private final float y;
    private final int color;

    public MapMarker(float x, float y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getColor() {
        return color;
    }
}