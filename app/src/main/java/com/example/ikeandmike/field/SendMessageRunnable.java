package com.example.ikeandmike.field;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by peter on 10/24/15.
 */
public class SendMessageRunnable implements Runnable {

    private OutputStream os;
    private byte[] packet;

    SendMessageRunnable(OutputStream os, BTProtocol.Type type, byte fromId, byte toId, byte data){
        this.os = os;
        byte[] dataArr = new byte[1];
        dataArr[0] = data;
        packet = BTProtocol.createPacket(type, fromId, toId, dataArr);
    }

    SendMessageRunnable(OutputStream os, BTProtocol.Type type, byte fromId, byte toId, byte[] data) {
        packet = BTProtocol.createPacket(type, fromId, toId, data);
    }

    @Override
    public void run(){
        try {
            os.write(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
