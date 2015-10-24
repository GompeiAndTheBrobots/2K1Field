package com.example.ikeandmike.field;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Main extends AppCompatActivity implements BluetoothCallback{

    private ScheduledThreadPoolExecutor fieldDataExecutor;
    private FieldUSBCommunicator fieldComms;
    private BTCommunicator comms = BTCommunicator.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        setupBluetooth();
    }

    private void setupBluetooth(){
        if (comms.exists()) {
            if (comms.enabled()){
                if (comms.detected()){
                    // this is asynchronous, and it should respond somehow...
                    comms.connect();
                    comms.addConnectorListener(this);
                }
                else {
                    Toast.makeText(this, "No robot found!", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                this.startActivityForResult(enableBtIntent, BTProtocol.REQUEST_ENABLE_BT);
            }
        }
        else {
            Toast.makeText(this, "Your device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
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

    public void successfulConnect() {
        Toast.makeText(Main.this, "bluetooth connected", Toast.LENGTH_SHORT).show();
        comms.asyncSendFieldData();
    }

    @Override
    public void failedConnect() {
        Toast.makeText(Main.this, "bluetooth not connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        comms.close();
    }

    public void updateState(View view) {
        Byte mask = 0;
        switch(view.getId()) {
            case R.id.Storage1:
                Log.e("Success:", "Storage 1 Pressed");
                mask = 1 << 4;
                break;
            case R.id.Storage2:
                Log.e("Success:", "Storage 2 Pressed");
                mask = 1 << 5;
                break;
            case R.id.Storage3:
                Log.e("Success:", "Storage 3 Pressed");
                mask = 1 << 6;
                break;
            case R.id.Storage4:
                Log.e("Success:", "Storage 4 Pressed");
                mask = (byte) -128;
                break;
            case R.id.Supply1:
                Log.e("Success:", "Supply 1 Pressed");
                mask = 1;
                break;
            case R.id.Supply2:
                Log.e("Success:", "Supply 2 Pressed");
                mask = 1 << 1;
                break;
            case R.id.Supply3:
                Log.e("Success:", "Supply 3 Pressed");
                mask = 1 << 2;
                break;
            case R.id.Supply4:
                Log.e("Success:", "Supply 4 Pressed");
                mask = 1 << 3;
                break;

            case R.id.toggleField:
                Log.e("Success:", "Field Access Toggled");
                break;
            case R.id.Resume:
                Log.e("Success:", "Resume Button Pressed");
                break;
            case R.id.Stop:
                Log.e("Success", "Stop Button Pressed");
                break;
            default:
                break;
        }
        if (mask != 0) {
            Log.d("Mask:", Integer.toBinaryString(mask));
            FieldState state = FieldState.getInstance();
            Byte field = state.getStorageSupplyByte();
            Log.d("Field Before:", Integer.toBinaryString(field));
            Byte newField = (byte)(field ^ mask);
            state.setStorageSupplyByte(newField);
            field = state.getStorageSupplyByte();
            Log.d("Field After:", Integer.toBinaryString(field));
        }
    }
}

