package edu.wpi.rbe2001.field;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Main extends AppCompatActivity implements BluetoothConnectionCallback,
        BluetoothMessageCallback,
        View.OnClickListener,
        Animation.AnimationListener,
        FieldStateChangeInterface {

    private static final long SPAM_TIME = 100l;
    private BTCommunicator comms = BTCommunicator.getInstance();
    private FieldStateInterface fieldStateInterface;

    private ImageView heartbeatIndicator;
    private TextView radiationInfo;
    private Loggers loggers;
    private Animation animation;
    private Button stopButton, resumeButton, resetButton;

    private long lastMsgTime = 0L;


    private boolean useFieldData = true;

    public static int[] buttonIds = new int[]{
            R.id.Supply1, R.id.Supply2, R.id.Supply3, R.id.Supply4,
            R.id.Storage1, R.id.Storage2, R.id.Storage3, R.id.Storage4
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        fieldStateInterface = new FieldStateInterface();
        FieldState.getInstance().registerFieldStateChangeListener(this);

        heartbeatIndicator = (ImageView) findViewById(R.id.heartbeart_indicator);
        stopButton = (Button) findViewById(R.id.stop);
        resumeButton = (Button) findViewById(R.id.resume);
        radiationInfo = (TextView) findViewById(R.id.radiation_info);
        resetButton = (Button) findViewById(R.id.resetButton);

        loggers = new Loggers(this);

        stopButton.setOnClickListener(this);
        resumeButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        for (int id : buttonIds) {
            (findViewById(id)).setOnClickListener(fieldStateInterface);
        }

        animation = new AlphaAnimation(1, 1);
        animation.setDuration(100);
        animation.setInterpolator(new LinearInterpolator());
        animation.setAnimationListener(this);

        setupBluetooth();

        //also, turn on field sending
        BTCommunicator.sendingFieldData = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                DeviceDialog d = new DeviceDialog();
                d.show(getSupportFragmentManager(), "Device Dialog");
                return true;
        }
        return false;
    }

    private void setupBluetooth() {
        if (comms.exists()) {
            if (comms.enabled()) {
                // get the bluetooth device name from settings
                SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
                String robot_name = pref.getString(getString(R.string.robot_name), null);
                if (robot_name == null) {
                    // show dialog
                    DeviceDialog dialog = new DeviceDialog();
                    dialog.show(getSupportFragmentManager(), "Devices Dialog");
                }
                else {
                    boolean success = comms.connectToDevice(robot_name);
                    if (!success) {
                        Toast.makeText(Main.this, "Couldn't connect to " + robot_name +
                                        ". Reselect your robot.",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // this is asynchronous, and it should respond somehow...
                        if (!comms.isConnected()) {
                            comms.connect();
                            comms.addConnectorListener(this);
                            comms.addOnMessageListener(this);
                        } else {
                            Toast.makeText(Main.this, "Already connected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                this.startActivityForResult(enableBtIntent, BTProtocol.REQUEST_ENABLE_BT);
            }
        } else {
            Toast.makeText(this, "Your device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
        }
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
    }

    @Override
    protected void onPause() {
        super.onPause();
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
            //Run animation and set color
            heartbeatIndicator.setBackgroundColor(Color.RED);
            heartbeatIndicator.startAnimation(animation);

        } else if (type == BTProtocol.Type.ALERT) {
            indicateRadiation(data);

        } else if (type == BTProtocol.Type.STATUS || type == BTProtocol.Type.DEBUG) {
            long t = System.currentTimeMillis();
            long dt = t - lastMsgTime;
            if (dt > SPAM_TIME) {
                lastMsgTime = t;
                String msg = "";
                if (type == BTProtocol.Type.STATUS) {
                    msg = BTProtocol.statusString(data);
                }
                else {
                    for (Byte b : data) {
                        msg += (char) (b & 0xFF);
                    }
                }
                loggers.append(msg);
            }
        }
    }

    @Override
    public void invalidMessage(String msg) {
        loggers.append(msg);
    }

    private void indicateRadiation(byte data[]) {
        if (data[0] == BTProtocol.HIGH_RADIATION) {
            radiationInfo.setText(R.string.high);
        } else if (data[0] == BTProtocol.LOW_RADIATION) {
            radiationInfo.setText(R.string.low);
        } else {
            radiationInfo.setText(R.string.unkown);
        }
    }

    @Override
    public void robotDisconnected() {
        Toast.makeText(Main.this, "Robot disconnected", Toast.LENGTH_SHORT).show();
        comms.close();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.resume) {
            comms.asyncSendResumeMessage();
        } else if (v.getId() == R.id.stop) {
            comms.asyncSendStopMessage();
        } else if (v.getId() == R.id.resetButton) {
            setupBluetooth();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        heartbeatIndicator.setBackgroundColor(Color.BLACK);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    //Update the indicators for the storage and supply
    @Override
    public void onFieldStateChange(Byte supplyStorageByte) {
        Handler handler = new Handler(this.getMainLooper());
        final Byte data = supplyStorageByte;
        //Run on UI Thread to update the toggles
        handler.post(new Runnable() {
            public void run() {
                if (useFieldData) {
                    for (int i = 0; i < buttonIds.length; i++) {
                        byte mask = ((Integer) (1 << i)).byteValue();
                        boolean state = ((mask & data) != 0);
                        ((ToggleButton) findViewById(buttonIds[i])).setChecked(state);
                    }
                }
            }
        });
    }
}
