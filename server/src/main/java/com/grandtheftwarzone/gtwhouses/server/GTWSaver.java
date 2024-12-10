package com.grandtheftwarzone.gtwhouses.server;

import com.grandtheftwarzone.gtwhouses.common.GTWSerializable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GTWSaver<T extends GTWSerializable> {
    private final File saveFile;
    private final Class<T> clazz;

    public GTWSaver(String fileName, Class<T> clazz) {
        saveFile = new File(GTWHouses.getInstance().getDataFolder(), fileName);
        this.clazz = clazz;
    }


    public void save(Collection<T> objects) throws Exception {
        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        FileOutputStream writer = new FileOutputStream(saveFile);
        ObjectOutputStream buffer = new ObjectOutputStream(writer);

        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(objects.size());
        for (T o : objects)
            o.toBytes(buf);

        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        buffer.writeObject(bytes);


        buffer.flush();
        buffer.close();
    }

    public Collection<T> load() throws Exception {
        if (!saveFile.exists())
            return Collections.emptyList();

        FileInputStream reader = new FileInputStream(saveFile);
        ObjectInputStream buffer = new ObjectInputStream(reader);

        byte[] bytes = (byte[]) buffer.readObject();
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);

        int size = buf.readInt();
        List<T> objects = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            T o = clazz.getDeclaredConstructor().newInstance();
            o.fromBytes(buf);
            objects.add(o);
        }

        buffer.close();
        return objects;
    }
}
