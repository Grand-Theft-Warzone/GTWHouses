package com.grandtheftwarzone.gtwhouses.client.network;

import com.grandtheftwarzone.gtwhouses.common.network.IGTWPacket;

public interface GTWHouseHandler<T extends IGTWPacket> {
    void handle(T message);
}
