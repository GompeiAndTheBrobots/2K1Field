package com.example.ikeandmike.field;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BTConnector extends Thread {

    private BluetoothSocket socket;
    private BluetoothDevice device;

    public BTConnector(BluetoothDevice device) {
        this.device = device;
        BluetoothSocket tmp = null;
        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            tmp = device.createInsecureRfcommSocketToServiceRecord(UUID.randomUUID());
        } catch (IOException e) {
            Log.e("BTConnector", "failed to create service record");
        }
        socket = tmp;
    }

    public void run() {
        try {
            socket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket",
                    new Class[] {int.class}).invoke(device,1);
            socket.connect();
        } catch (Exception connectException) {
            try {
                socket.close();
            } catch (IOException closeException) {}
            Log.e("BT Connector", "Bluetooth failed to connect!");
            return;
        }

        // Do work to manage the connection (in a separate thread)

    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) { }
    }
}