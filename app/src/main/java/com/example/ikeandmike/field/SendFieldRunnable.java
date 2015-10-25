package com.example.ikeandmike.field;

import com.hoho.android.usbserial.driver.ProbeTable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by peter on 10/24/15.
 */
public class SendFieldRunnable implements  Runnable{

    final private OutputStream os;

    public SendFieldRunnable(OutputStream os){
        this.os = os;
    }

    @Override
    public void run(){
        byte[] supplyData = new byte[1];
        byte[] storageData = new byte[1];
        byte data = FieldState.getInstance().getStorageSupplyByte();
        storageData[0] = (byte)(data & 0x0F);
        supplyData[0] = (byte)(data & 0xF0);

        byte[] storagePacket = BTProtocol.createPacket(BTProtocol.Type.FIELD, (byte) 0,
                (byte) BTProtocol.TeamNumber, storageData);
        byte[] supplPacket = BTProtocol.createPacket(BTProtocol.Type.FIELD, (byte) 0,
                (byte) BTProtocol.TeamNumber, storageData);

        try {
            os.write(supplPacket);
            os.write(storagePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
