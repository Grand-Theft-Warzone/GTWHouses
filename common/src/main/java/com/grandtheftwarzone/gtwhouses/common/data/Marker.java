package com.grandtheftwarzone.gtwhouses.common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Marker {
    private final int x;
    private final int z;

    private final String houseName;

    public static Marker fromHouse(House house) {
        int x = house.getMaxPosX() - (house.getMaxPosX() - house.getMinPosX()) / 2;
        int z = house.getMaxPosZ() - (house.getMaxPosZ() - house.getMinPosZ()) / 2;

        return new Marker(x, z, house.getName());
    }
}
