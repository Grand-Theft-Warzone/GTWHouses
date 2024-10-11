package com.grandtheftwarzone.gtwhouses.client.houseimages;

import com.grandtheftwarzone.gtwhouses.common.data.House;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HouseImagesManager {
    private static final Map<String, HouseImage> houseImages = new HashMap<>();

    public static void loadImages(List<String> urls) {
        for (String url : urls) {
            String key = getKey(url);
            HouseImagesLoader.loadImageAsync(key, url, texture -> houseImages.put(key, texture));
        }
    }

    public static HouseImage getImage(String url) {
        return houseImages.get(getKey(url));
    }

    public static HouseImage getImage(House house) {
        return getImage(house.getImageURL());
    }

    private static String getKey(String url) {
        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(url.getBytes());
            return String.format("%032x", new java.math.BigInteger(1, hash));
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
