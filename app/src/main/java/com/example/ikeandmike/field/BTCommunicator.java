package com.example.ikeandmike.field;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by peter on 10/24/15.
 */
public class BTCommunicator implements BluetoothConnectionCallback {

    private BluetoothDevice robot;
    private BluetoothAdapter bTAdapter;
    private ScheduledThreadPoolExecutor fieldDataExecutor;
    private BTConnector connector;
    private InputStream is;
    private OutputStream os;
    private ReadRobotDataTask readDataTask;
    private List<BluetoothMessageCallback> listeners;

    private static BTCommunicator instance;

    public static BTCommunicator getInstance() {
        if (instance == null) {
            instance = new BTCommunicator();
            instance.listeners = new ArrayList<>();
        }
        return instance;
    }

    public void addOnMessageListener(BluetoothMessageCallback listener){
        listeners.add(listener);
    }

    private BTCommunicator() {
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
                if (foundRobot(device)) {
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
                    BTProtocol.TeamNumber = Integer.parseInt(matcher.group(1));
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

    public void addConnectorListener(BluetoothConnectionCallback listener) {
        connector.addListener(listener);
    }

    public void connect() {
        // spawn a new thread to try connecting
        connector = new BTConnector(robot);
        connector.addListener(this);
        connector.execute();
    }

    public void close() {
        connector.close();
    }

    public void asyncSendStopMessage() {
        fieldDataExecutor.execute(new SendMessageRunnable(os, BTProtocol.Type.STOP, (byte) 0));
    }

    public void asyncSendResumeMessage() {
        fieldDataExecutor.execute(new SendMessageRunnable(os, BTProtocol.Type.RESUME, (byte) 0));
    }

    public void asyncSendFieldData() {
        // start thread for sending BT data
        fieldDataExecutor = new ScheduledThreadPoolExecutor(8);

        //call run() on SendFieldRunnable every 10ms
        fieldDataExecutor.scheduleAtFixedRate(
                new SendFieldRunnable(os),
                0l,
                100,
                TimeUnit.MILLISECONDS);
    }

    public void asyncReadRobotData(){
        readDataTask = new ReadRobotDataTask(is, listeners);
        readDataTask.execute();
    }

    public void stopListeneing(){
        readDataTask.cancel(true);
    }

    @Override
    public void successfulConnect() {
        //we now have valid input and output streams
        is = connector.is;
        os = connector.os;
        asyncSendFieldData();
        asyncReadRobotData();
    }

    @Override
    public void failedConnect() {
        //nothing to do here
    }
}
