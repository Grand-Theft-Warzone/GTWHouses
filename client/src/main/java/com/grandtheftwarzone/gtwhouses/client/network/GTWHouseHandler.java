package com.grandtheftwarzone.gtwhouses.client.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface GTWHouseHandler<T extends IMessage > {
    void handle(T message);
}
