package edu.wpi.rbe.field;

/**
 * Encapsulates functionality to create/destroy connections, and communicate with
 * the field microcontroller over a serial connection.
 * @author Jordan Burklund
 * @date 10/24/2015
 * Created by jordanbrobots on 10/24/15.
 */

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FieldUSBCommunicator {
    private Context appContext;

    private UsbSerialDriver driver;
    private UsbSerialPort fieldPort;
    private SerialInputOutputManager mSerialIoManager;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private FieldState fieldState;

    private String TAG = this.getClass().getName();

    /** Constructor
     * @param context The context of the calling application
     */
    public FieldUSBCommunicator(Context context) {
        appContext = context;
        fieldState = FieldState.getInstance();
    }

    /**
     * Handle any actions that need to happen when an activity is resumed
     */
    public void onResume() {
        Log.d(TAG, "Opening a new device and port");

        //Get a new temporary reference to the usbManager
        final UsbManager usbManager = (UsbManager) appContext.getSystemService(Context.USB_SERVICE);

        //Get a list of all available devices
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
        if(availableDrivers.isEmpty()) {
            //No devices were detected, exit early
            fieldPort = null;
            return;
        }

        //Attempt to open a connection to the first device
        driver = availableDrivers.get(0);
        UsbDeviceConnection connection = usbManager.openDevice(driver.getDevice());
        if (connection == null) {
            Log.d(TAG, "Opening device failed");
            return;
        }

        //Get the first "port" on the device which is typically the serial line
        fieldPort = driver.getPorts().get(0);
        Log.d(TAG, "Opening a new port: " + fieldPort);

        try {
            //Connect to the port, and set the UART parameters
            fieldPort.open(connection);
            fieldPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        } catch (IOException e) {
            Log.e(TAG, "Error during device setup: " + e.getMessage(), e);
            //Close the port
            closePort();
            return;
        }
        restartIoManager();
    }

    //#TODO change to onCreate and onDestroy so that connection is maintained
    /**
     * Handle any actions that need to happen when an activity is paused
     */
    public void onPause() {
        Log.d(TAG, "Pausing Field Communications");
        stopIoManager();
        closePort();
    }

    /**
     * Closes the connection to the field device
     * #TODO There seems to be some weird errors when no phone is connected and this method is called
     */
    private void closePort() {
        Log.d(TAG, "Closing port");
        try {
            fieldPort.close();
        } catch (Exception e) {
            //Ignore any exceptions when closing
        }
        fieldPort = null;
    }

    /**
     * Stops the IoManager to stop reading data events
     */
    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
            mExecutor.shutdownNow();
        }
    }

    /**
     * Starts the IoManager to start reading data events
     */
    private void startIoManager() {
        if (fieldPort != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(fieldPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    /**
     * Handle various events for errors, and when data is received
     */
    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                    //Do something on an error
                }

                @Override
                public void onNewData(final byte[] data) {
                    //Do something when new data is received
                    final String message = "Read " + data.length + " bytes: "
                            + HexDump.dumpHexString(data) + "\n\n";
                    Log.d(TAG, message);
                    fieldState.setStorageSupplyByte(data[0]);
                }
            };

    /**
     * Restarts the IoManager.  Typically used
     * when the app is restarted.
     */
    private void restartIoManager() {
        stopIoManager();
        startIoManager();
    }

    /**
     * Checks if the device is connected
     * @return True if a device is connected
     */
    public boolean isConnected() {
        return fieldPort != null;
    }
}
