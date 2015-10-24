package com.example.ikeandmike.field;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Main extends AppCompatActivity {

    ScheduledThreadPoolExecutor fieldDataExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        //check if you don't have bluetooth enabled
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(Main.this, "You don't have bluetooth!", Toast.LENGTH_SHORT).show();
        }
        else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, BTProtocol.REQUEST_ENABLE_BT);
            }
            else {
                // you are good to go!
                // start thread for sending BT data
                fieldDataExecutor = new ScheduledThreadPoolExecutor(8);

                //call run() on SendMessageRunnable every 10ms
                fieldDataExecutor.scheduleAtFixedRate(
                        new SendMessageRunnable(BTProtocol.Type.FIELD, (byte) 0),
                        0l,
                        100,
                        TimeUnit.MILLISECONDS);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case BTProtocol.REQUEST_ENABLE_BT:
                //the user has returned from the enable BT dialog
                if (resultCode == Activity.RESULT_OK){
                    // yay bluetooth should be enabled!
                    // but it might not be paired...
                }
                else {
                    //god damn it...
                    Toast.makeText(Main.this, "You really need bluetooth enabled...", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
