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

    public SendMessageRunnable(OutputStream os, BTProtocol.Type type, byte[] data){
        this.os = os;
        this.type = type;
        this.data = data;
    }

    public SendMessageRunnable(OutputStream os, BTProtocol.Type type, byte data){
        this.os = os;
        this.type = type;
        this.data = new byte[1];
        this.data[0] = data;
    }

    @Override
    public void run(){
        byte[] allData = new byte[type.length()+1];

        //construct packet based on BT spec
        allData[0] = 0x5F;
        allData[1] = type.length();
        allData[2] = type.id();
        allData[3] = (byte)0x0;
        allData[4] = (byte)BTProtocol.TeamNumber;

        //send the actual data
        int i = 5;
        for (;i<5+this.data.length; i++) {
            allData[i] = this.data[i-5];
        }

        allData[i] = calcChecksum();

        try {
            os.write(allData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte calcChecksum(){
        // 0xff minus the 8 bit sum of bytes from
        // offset 1 up to but no including this byte
//        byte b = ;
//        for (byte b : )
        
        return (byte)0;
    }
}
