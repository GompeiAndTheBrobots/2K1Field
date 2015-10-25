package com.example.ikeandmike.field;

import android.util.Log;

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

        byte[] storagePacket = BTProtocol.createPacket(BTProtocol.Type.STORAGE, (byte) 0,
                (byte) 0x0, storageData);
        byte[] supplyPacket = BTProtocol.createPacket(BTProtocol.Type.SUPPLY, (byte) 0,
                (byte )0x0, supplyData);

        try {
            os.write(supplyPacket);
            Thread.sleep(500);
            os.write(storagePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
