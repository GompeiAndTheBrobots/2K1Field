package edu.wpi.rbe.field;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Main extends AppCompatActivity implements BluetoothConnectionCallback,
        BluetoothMessageCallback,
        View.OnClickListener,
        Animation.AnimationListener,
        FieldStateChangeInterface {

    private static final long SPAM_TIME = 100l;
    private BTCommunicator comms = BTCommunicator.getInstance();
    private FieldStateInterface fieldStateInterface;

    private RadioButton heartbeatIndicator;
    private GestureDetectorCompat mDetector;
    private ImageView radiationIndicator;
    private Loggers loggers;
    private Animation animation;
    private Button stopButton, resumeButton, resetButton;

    private long lastStatusTime = 0l;
    private long lastDebugTime = 0l;


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

        heartbeatIndicator = (RadioButton) findViewById(R.id.heartbeatIndicator);
        stopButton = (Button) findViewById(R.id.stop);
        resumeButton = (Button) findViewById(R.id.resume);
        radiationIndicator = (ImageView) findViewById(R.id.radiationIndicator);
        resetButton = (Button) findViewById(R.id.resetButton);

        loggers = new Loggers(this);

        mDetector = new GestureDetectorCompat(this,
                new SimpleGestureListener(this,
                        BluetoothTester.class,
                        SimpleGestureListener.RIGHT));

        stopButton.setOnClickListener(this);
        resumeButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        for (int id : buttonIds) {
            ((Button) findViewById(id)).setOnClickListener(fieldStateInterface);
        }

        animation = new AlphaAnimation(1, 0);
        animation.setDuration(50);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(1);
        animation.setAnimationListener(this);

        setupBluetooth();

        //also, turn on field sending
        BTCommunicator.sendingFieldData = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void setupBluetooth() {
        if (comms.exists()) {
            if (comms.enabled()) {
                if (comms.detected()) {
                    // this is asynchronous, and it should respond somehow...
                    if (!comms.isConnected()) {
                        comms.connect();
                        comms.addConnectorListener(this);
                        comms.addOnMessageListener(this);
                    } else {
                        Toast.makeText(Main.this, "Already connected", Toast.LENGTH_SHORT).show();
                    }
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
            heartbeatIndicator.setChecked(!heartbeatIndicator.isChecked());

        } else if (type == BTProtocol.Type.ALERT) {
            indicateRadiation(data);

        } else if (type == BTProtocol.Type.STATUS) {
            long t = System.currentTimeMillis();
            long dt = t - lastStatusTime;
            if (dt > SPAM_TIME) {
                lastStatusTime = t;
                String status = BTProtocol.statusString(data);
                loggers.appendStatus(status);
            }

        } else if (type == BTProtocol.Type.DEBUG) {
            long t = System.currentTimeMillis();
            long dt = t - lastDebugTime;
            if (dt > SPAM_TIME) {
                lastDebugTime = t;
                String debug = "";
                for (Byte b : data) {
                    debug += (char) (b & 0xFF);
                }
                loggers.appendDebug(debug);
            }
        }
    }

    private void indicateRadiation(byte data[]) {
        if (data[0] == BTProtocol.HIGH_RADIATION) {
            //Low Radiation -- Yellow
            radiationIndicator.setBackgroundColor(Color.YELLOW);
        } else if (data[0] == BTProtocol.LOW_RADIATION) {
            //High Radiation -- Red
            radiationIndicator.setBackgroundColor(Color.RED);
        } else {
            radiationIndicator.setBackgroundColor(Color.BLUE);
        }
        //Run animation
        radiationIndicator.startAnimation(animation);
    }

    @Override
    public void robotDisconnected() {
        Toast.makeText(Main.this, "Robot disconnected", Toast.LENGTH_SHORT).show();
        comms.close();
    }

    @Override
    public void invalidMessage() {
        Toast.makeText(Main.this, "Invalid message were received!", Toast.LENGTH_SHORT).show();
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
        radiationIndicator.setBackgroundColor(Color.BLACK);
        radiationIndicator.clearAnimation();
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
