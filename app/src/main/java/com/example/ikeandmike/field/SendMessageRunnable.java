package com.example.ikeandmike.field;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by peter on 10/24/15.
 */
public class SendMessageRunnable implements  Runnable{

    BTProtocol.Type type;
    byte data[];
    private OutputStream os;
    byte[] packet;

    public SendMessageRunnable(OutputStream os, BTProtocol.Type type, byte[] data){
        this.os = os;
        this.type = type;
        this.data = data;
        packet = new byte[type.length()+1];
    }

    public SendMessageRunnable(OutputStream os, BTProtocol.Type type, byte data){
        this.os = os;
        this.type = type;
        this.data = new byte[1];
        this.data[0] = data;
        packet = new byte[type.length()+1];
    }

    @Override
    public void run(){

        //construct packet based on BT spec
        packet[0] = 0x5F;
        packet[1] = type.length();
        packet[2] = type.id();
        packet[3] = (byte)0x0;
        packet[4] = (byte)BTProtocol.TeamNumber;

        //send the actual data
        int i = 5;
        for (;i<5+this.data.length; i++) {
            packet[i] = this.data[i-5];
        }

        packet[i] = calcChecksum();

        try {
            os.write(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte calcChecksum(){
        // 0xff minus the 8 bit sum of bytes from
        // offset 1 up to but no including this byte
        byte sum = 0;
        for (byte b : packet){
            sum += b;
        }

        return (byte)(0xff - sum);
    }
}
