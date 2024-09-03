package com.grandtheftwarzone.gtwhouses.common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class HouseBlock implements Serializable {
    private int x;
    private int y;
    private int z;
}
