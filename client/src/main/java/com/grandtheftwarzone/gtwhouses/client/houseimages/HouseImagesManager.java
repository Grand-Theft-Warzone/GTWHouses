package com.grandtheftwarzone.gtwhouses.client.houseimages;

import com.grandtheftwarzone.gtwhouses.common.data.House;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HouseImagesManager {
    private static final Map<String, HouseImage> houseImages = new HashMap<>();

    public static void loadImages(List<String> urls) {
        for (String url : urls) {
            String key = Base64.getEncoder().encodeToString(url.getBytes());

            HouseImagesLoader.loadImageAsync(key, url, texture -> houseImages.put(key, texture));
        }
    }

    public static HouseImage getImage(String url) {
        String key = Base64.getEncoder().encodeToString(url.getBytes());
        return houseImages.get(key);
    }

    public static HouseImage getImage(House house) {
        return getImage(house.getImageURL());
    }
}
