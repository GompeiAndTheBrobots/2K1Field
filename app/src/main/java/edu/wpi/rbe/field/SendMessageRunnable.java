package edu.wpi.rbe.field;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by peter on 10/24/15.
 */
public class SendMessageRunnable implements Runnable {

    private OutputStream os;
    private byte[] packet;

    SendMessageRunnable(OutputStream os, BTProtocol.Type type, byte fromId, byte toId){
        this.os = os;
        packet = BTProtocol.createPacket(type, fromId, toId, new byte[0]);
    }

    SendMessageRunnable(OutputStream os, BTProtocol.Type type, byte fromId, byte toId, byte[] data) {
        packet = BTProtocol.createPacket(type, fromId, toId, data);
    }

    @Override
    public void run(){
        try {
            String str = "";
            for (byte b : packet){
                str += Byte.toString(b) +" ";
            }
            os.write(packet);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("SendMessageRunnable","send message runnable failed");
        }
    }
}
