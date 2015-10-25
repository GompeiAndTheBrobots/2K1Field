package com.example.ikeandmike.field;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class BluetoothTester extends AppCompatActivity {
    private EditText fromIdInput;
    private EditText toIdInput;
    private EditText dataInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_tester);
        fromIdInput = (EditText) findViewById(R.id.FromID);
        toIdInput = (EditText) findViewById(R.id.ToID);
        dataInput = (EditText) findViewById(R.id.extradata);
    }

    public void sendPacket(View view) {
        try {
            byte fromId = Byte.decode(fromIdInput.getText().toString());
            byte toId = Byte.decode(toIdInput.getText().toString());
            //byte[] data =   DatatypeConverter.parseHexBinary(dataInput.getText().toString());
        } catch (NumberFormatException e) {
            //Handle the exception when the data in the fields is not parseable
            return;
        }


    }

}
