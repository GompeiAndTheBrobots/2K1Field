package com.example.ikeandmike.field;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by peter on 10/24/15.
 */
public class BTCommunicator {

    private BluetoothDevice robot;
    private int teamNumber;
    private BluetoothAdapter bTAdapter;
    private Context mainContext;
    private ScheduledThreadPoolExecutor fieldDataExecutor;
    private BTConnector connector;

    BTCommunicator(Context mainContext) {
        this.mainContext = mainContext;

        //check if you don't have bluetooth enabled
        bTAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    public boolean exists() {
        return bTAdapter != null;
    }

    public boolean enabled() {
        return bTAdapter.isEnabled();
    }

    public boolean detected() {
        Set<BluetoothDevice> pairedDevices = bTAdapter.getBondedDevices();

        // If there are paired devices
        if (pairedDevices.size() > 0) {

            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                if (foundRobot(device)){
                    return true;
                }
            }
        }

        return false;
    }

    public boolean foundRobot(BluetoothDevice device) {
        if (device.getName().contains("RBE")) {
            Pattern teamNumberPattern = Pattern.compile("RBE_BT([0-9]*)");
            Matcher matcher = teamNumberPattern.matcher(device.getName());
            if (matcher.find()) {
                try {
                    robot = device;
                    ParcelUuid[] ids = robot.getUuids();
                    for (ParcelUuid id : ids){
                        Log.e("UUID",id.getUuid().toString());
                    }

                    teamNumber = Integer.parseInt(matcher.group(1));
                    Toast.makeText(mainContext, "Robot " + teamNumber + " found!", Toast.LENGTH_SHORT).show();
                    return true;
                } catch (NumberFormatException e) {
                    // no way will this actually happen...right?
                    e.printStackTrace();
                }
            } else {
                Log.w("not team #", device.getName());
            }
        }
        return false;

    }

    public void connect(){

        // spawn a new thread to try connecting
        connector = new BTConnector(robot);
        connector.start();

//        // start thread for sending BT data
//        fieldDataExecutor = new ScheduledThreadPoolExecutor(8);
//
//        //call run() on SendMessageRunnable every 10ms
//        fieldDataExecutor.scheduleAtFixedRate(
//                new SendMessageRunnable(BTProtocol.Type.FIELD, (byte) 0),
//                0l,
//                100,
//                TimeUnit.MILLISECONDS);

    }
}
