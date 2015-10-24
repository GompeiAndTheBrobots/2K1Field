package com.example.ikeandmike.field;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by peter on 10/24/15.
 */
public class ReadMessageRunnable implements  Runnable{

    BTProtocol.Type type;
    byte data;
    private OutputStream os;

    public ReadMessageRunnable(OutputStream os, BTProtocol.Type type, byte data){
        this.os = os;
        this.type = type;
        this.data = data;
    }

    @Override
    public void run(){
        byte[] allData = new byte[3];
        allData[0] = data;
        try {
            os.write(allData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
