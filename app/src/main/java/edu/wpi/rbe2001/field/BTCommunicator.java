package edu.wpi.rbe2001.field;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.ArrayAdapter;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BTCommunicator implements BluetoothConnectionCallback {

    static boolean sendingFieldData = false;
    private BluetoothDevice robot;
    private BluetoothAdapter bTAdapter;
    private ScheduledThreadPoolExecutor fieldDataExecutor;
    private BTConnector connector;
    private InputStream is;
    private OutputStream os;
    private ReadRobotDataTask readDataTask;
    private List<BluetoothMessageCallback> listeners;
    private boolean connected;

    private static BTCommunicator instance;

    static BTCommunicator getInstance() {
        if (instance == null) {
            instance = new BTCommunicator();
            instance.listeners = new ArrayList<>();
        }
        return instance;
    }

    void addOnMessageListener(BluetoothMessageCallback listener){
        listeners.add(listener);
    }

    private BTCommunicator() {
        bTAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    boolean exists() {
        return bTAdapter != null;
    }

    boolean enabled() {
        return bTAdapter.isEnabled();
    }

    List<String> getDeviceNames() {
        Set<BluetoothDevice> pairedDevices = bTAdapter.getBondedDevices();
        ArrayList<String> names = new ArrayList<String>();

        // If there are paired devices
        if (pairedDevices.size() > 0) {

            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                names.add(device.getName());
            }
        }

        return names;
    }

    boolean connectToDevice(String device_name) {
        Set<BluetoothDevice> pairedDevices = bTAdapter.getBondedDevices();

        // If there are paired devices
        if (pairedDevices.size() > 0) {

            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                Log.w("Paired Device", device.getName());
                if (device.getName().equals(device_name)) {
                    robot = device;
                    BTProtocol.TeamNumber = BTCommunicator.extractRobotNumber(device_name);
                    Log.w("Team Number", String.valueOf(BTProtocol.TeamNumber));
                    return true;
                }
            }
        }

        return false;
    }

    static int extractRobotNumber(String device_name) {
        Pattern teamNumberPattern = Pattern.compile(".*?([0-9]+).*");
        Matcher matcher = teamNumberPattern.matcher(device_name);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                // no way will this actually happen...right?
                e.printStackTrace();
            }
        }

        return 0xff;
    }

    boolean isConnected(){
        return connected;
    }

    void addConnectorListener(BluetoothConnectionCallback listener) {
        connector.addListener(listener);
    }

    void connect() {
        // spawn a new thread to try connecting
        connector = new BTConnector(robot);
        connector.addListener(this);
        connector.execute();
    }

    void close() {
        connected = false;
        listeners.clear();

        try {
            fieldDataExecutor.shutdownNow();
            readDataTask.cancel(true);
            connector.close();
            is.close();
            os.close();
        } catch (Exception e) {
            // we don't care, since the user is closing the app.
        }
    }

    void asyncSendStopMessage() {
        if (fieldDataExecutor != null) {
            try {
                fieldDataExecutor.execute(new SendMessageRunnable(os,
                        BTProtocol.Type.STOP,
                        (byte) 0,
                        (byte) BTProtocol.TeamNumber));
            } catch (RejectedExecutionException re) { }
        }
    }

    void asyncSendResumeMessage() {
        if (fieldDataExecutor != null) {
            try {
                fieldDataExecutor.execute(new SendMessageRunnable(os,
                        BTProtocol.Type.RESUME,
                        (byte) 0,
                        (byte) BTProtocol.TeamNumber));
            } catch (RejectedExecutionException re) { }
        }
    }

    private void asyncSendFieldData() {
        // start thread for sending BT data
        fieldDataExecutor = new ScheduledThreadPoolExecutor(8);

        //call run() on SendFieldRunnable every 10ms
        fieldDataExecutor.scheduleAtFixedRate(
                new SendFieldRunnable(os),
                0l,
                100,
                TimeUnit.MILLISECONDS);
    }

    void stopListening(){
        if(readDataTask != null) {
            readDataTask.cancel(true);
        }
    }

    void asyncSendPacket(BTProtocol.Type msgType, byte fromID, byte toID, byte[] data) {
        fieldDataExecutor.execute(new SendMessageRunnable(os, msgType, fromID, toID, data));
    }

    private void asyncReadRobotData(){
        readDataTask = new ReadRobotDataTask(is, listeners);
        readDataTask.execute();
    }

    @Override
    public void successfulConnect() {
        //we now have valid input and output streams
        is = connector.is;
        os = connector.os;
        asyncSendFieldData();
        asyncReadRobotData();
        connected = true;
    }

    @Override
    public void failedConnect() {
        //nothing to do here
        Log.e(getClass().toString(), "DISCONNECTED!!!!!!!");
        connected = false;
    }
}
