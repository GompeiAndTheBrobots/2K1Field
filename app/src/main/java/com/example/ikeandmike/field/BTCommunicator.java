package com.example.ikeandmike.field;

import com.example.ikeandmike.field.BTProtocol;

/**
 * Created by peter on 10/24/15.
 */
public class BTCommunicator {

    public BTCommunicator(){}

    public void sendResume(){
        send(BTProtocol.Type.RESUME, (byte)0);
    }

    public void sendStop(){
        send(BTProtocol.Type.STOP, (byte)0);
    }

    public void sendFieldData(byte data){
        send(BTProtocol.Type.SUPPLY, data);
        send(BTProtocol.Type.STORAGE, data);
    }

    public void send(BTProtocol.Type type, byte data){
        //send the data!
        new BTTask(type, data).execute();
    }
}
