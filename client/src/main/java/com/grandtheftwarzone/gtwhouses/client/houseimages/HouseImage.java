package com.grandtheftwarzone.gtwhouses.client.houseimages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.ResourceLocation;

@Getter
@AllArgsConstructor
public class HouseImage {
    private final ResourceLocation texture;
    private final int width;
    private final int height;
}
