package com.example.ikeandmike.field;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Main extends AppCompatActivity {

    private ScheduledThreadPoolExecutor fieldDataExecutor;
    private BTCommunicator comms;
    private FieldUSBCommunicator fieldComms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        comms = new BTCommunicator(this);
        if (comms.exists()) {
            if (comms.enabled()){
                if (comms.detected()){
                    // this is asynchronous, and it should respond somehow...
                    comms.connect();
                }
                else {
                    Toast.makeText(Main.this, "No robot found!", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, BTProtocol.REQUEST_ENABLE_BT);
            }
        }
        else {
            Toast.makeText(Main.this, "Your device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
        }

        fieldComms = new FieldUSBCommunicator(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BTProtocol.REQUEST_ENABLE_BT:
                //the user has returned from the enable BT dialog
                if (resultCode == Activity.RESULT_OK) {
                    // yay bluetooth should be enabled!
                    // but it might not be paired...
                } else {
                    //god damn it...
                    Toast.makeText(Main.this, "You really need bluetooth enabled...", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fieldComms.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fieldComms.onPause();
    }
}

