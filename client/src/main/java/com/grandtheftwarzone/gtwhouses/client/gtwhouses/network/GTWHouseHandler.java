package com.grandtheftwarzone.gtwhouses.client.gtwhouses.network;

import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;

public interface GTWHouseHandler<T extends IGTWPacket> {
    void handle(T message);
}
