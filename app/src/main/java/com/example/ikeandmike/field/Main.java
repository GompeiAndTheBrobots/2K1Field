package com.example.ikeandmike.field;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Main extends AppCompatActivity implements BluetoothConnectionCallback,
        BluetoothMessageCallback,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, Animation.AnimationListener {

    private FieldUSBCommunicator fieldComms;
    private BTCommunicator comms = BTCommunicator.getInstance();
    private FieldStateInterface fieldStateInterface;

    private RadioButton heartbeatIndicator;
    private Button stopButton, resumeButton;
    private ToggleButton toggleField;
    private ImageView radiationIndicator;
    private Animation animation;

    public static int[] buttonIds = new int[] {
        R.id.Supply1, R.id.Supply2, R.id.Supply3, R.id.Supply4,
                R.id.Storage1, R.id.Storage2, R.id.Storage3, R.id.Storage4
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        fieldStateInterface = new FieldStateInterface();

        heartbeatIndicator = (RadioButton) findViewById(R.id.heartbeatIndicator);
        stopButton = (Button) findViewById(R.id.stop);
        resumeButton = (Button) findViewById(R.id.resume);
        this.toggleField = (ToggleButton) findViewById(R.id.toggleField);
        this.radiationIndicator = (ImageView) findViewById(R.id.radiationIndicator);

        stopButton.setOnClickListener(this);
        resumeButton.setOnClickListener(this);

        for (int id : buttonIds) {
            ((Button) findViewById(id)).setOnClickListener(fieldStateInterface);
        }

        animation = new AlphaAnimation(1, 0);
        animation.setDuration(50);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(1);
        animation.setAnimationListener(this);

        this.toggleField.setOnCheckedChangeListener(this);
        setupBluetooth();
    }

    private void setupBluetooth() {
        if (comms.exists()) {
            if (comms.enabled()) {
                if (comms.detected()) {
                    // this is asynchronous, and it should respond somehow...
                    comms.connect();
                    comms.addConnectorListener(this);
                    comms.addOnMessageListener(this);
                } else {
                    Toast.makeText(this, "No robot found!", Toast.LENGTH_LONG).show();
                }
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                this.startActivityForResult(enableBtIntent, BTProtocol.REQUEST_ENABLE_BT);
            }
        } else {
            Toast.makeText(this, "Your device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
        }

        fieldComms = new FieldUSBCommunicator(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BTProtocol.REQUEST_ENABLE_BT) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(Main.this, "You really need bluetooth enabled...", Toast.LENGTH_LONG).show();
            }
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

    }

    @Override
    public void failedConnect() {
        Toast.makeText(Main.this, "bluetooth not connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        comms.stopListening();
        comms.close();
    }

    @Override
    public void validMessage(BTProtocol.Type type, byte[] data) {
        if (type == BTProtocol.Type.HEARTBEAT) {
            heartbeatIndicator.setChecked(!heartbeatIndicator.isChecked());
        }
        else if (type == BTProtocol.Type.ALERT)  {
            if (data[0] == 0x2C) {
                //Low Radiation -- Yellow
                radiationIndicator.setBackgroundColor(Color.YELLOW);
            }
            else if (data[0] == 0xFF) {
                //High Radiation -- Red
                radiationIndicator.setBackgroundColor(Color.RED);
            }
            else {
                radiationIndicator.setBackgroundColor(Color.BLUE);
            }
            //Run animation
            radiationIndicator.startAnimation(animation);
        }
    }

    @Override
    public void invalidMessage() {
        Toast.makeText(Main.this, "Invalid message we received!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.resume) {
            comms.asyncSendResumeMessage();
        } else if (v.getId() == R.id.stop) {
            comms.asyncSendStopMessage();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        for (int id : buttonIds) {
            if (isChecked) {
                findViewById(id).setEnabled(true);
            } else {
                ((ToggleButton) findViewById(id)).setChecked(false);
                ((ToggleButton) findViewById(id)).setEnabled(false);
                FieldState state = FieldState.getInstance();
                state.setStorageSupplyByte((byte) 0);
            }
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        radiationIndicator.setBackgroundColor(Color.BLACK);
        radiationIndicator.clearAnimation();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}
