package com.grandtheftwarzone.gtwhouses.client.houseimages;

import com.grandtheftwarzone.gtwhouses.client.GTWHousesUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HouseImagesLoader {
    private static final File CACHE_DIR = new File(Minecraft.getMinecraft().gameDir, "mods/" + GTWHousesUI.MODID + "/cache/");
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void loadImageAsync(String key, String url, ImageCallback callback) {
        if (isImageCached(key)) {
            BufferedImage bufferedImage = loadImageFromDisk(key);
            if (bufferedImage != null) {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    callback.onImageLoaded(uploadImageToTexture(bufferedImage, key));
                });
                return;
            }
        }

        executorService.submit(() -> {
            BufferedImage bufferedImage = loadImageFromURL(url);
            if (bufferedImage != null) {
                saveImageToDisk(bufferedImage, key);

                Minecraft.getMinecraft().addScheduledTask(() -> {
                    callback.onImageLoaded(uploadImageToTexture(bufferedImage, key));
                });
            }
        });
    }

    private static BufferedImage loadImageFromURL(String url) {
        try {
            URL imageUrl = new URL(url);
            InputStream inputStream = imageUrl.openStream();
            return ImageIO.read(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HouseImage uploadImageToTexture(BufferedImage bufferedImage, String key) {
        DynamicTexture dynamicTexture = new DynamicTexture(bufferedImage);
        ResourceLocation location = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("house:" + key, dynamicTexture);
        return new HouseImage(location, bufferedImage.getWidth(), bufferedImage.getHeight());
    }

    public static void saveImageToDisk(BufferedImage image, String key) {
        if (!CACHE_DIR.exists()) {
            CACHE_DIR.mkdirs();
        }

        try {
            File file = new File(CACHE_DIR, key + ".png");
            ImageIO.write(image, "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isImageCached(String key) {
        return new File(CACHE_DIR, key + ".png").exists();
    }

    public static BufferedImage loadImageFromDisk(String key) {
        try {
            File file = new File(CACHE_DIR, key + ".png");
            return ImageIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface ImageCallback {
        void onImageLoaded(HouseImage image);
    }
}
