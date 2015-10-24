package com.example.ikeandmike.field;


import android.os.AsyncTask;

/**
 * Created by peter on 10/24/15.
 */
public class SendMessageRunnable implements  Runnable{

    BTProtocol.Type type;
    byte data;

    public SendMessageRunnable(BTProtocol.Type type, byte data){
        this.type = type;
        this.data = data;
    }

    @Override
    public void run(){

    }
}
