package com.example.ikeandmike.field;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class BluetoothTester extends AppCompatActivity {
    private EditText fromIdInput;
    private EditText toIdInput;
    private EditText dataInput;

    private BTCommunicator btCommunicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_tester);
        fromIdInput = (EditText) findViewById(R.id.FromID);
        toIdInput = (EditText) findViewById(R.id.ToID);
        dataInput = (EditText) findViewById(R.id.extradata);
    }

    public void sendPacket(View view) {
        String fromIdInputString = fromIdInput.getText().toString();
        String toIdInputString = toIdInput.getText().toString();
        String dataInputString = dataInput.getText().toString();
        Toast.makeText(this, fromIdInputString + "\n"
                            + toIdInputString + "\n"
                            + dataInputString, Toast.LENGTH_SHORT).show();

        //Require a From ID number
        if(fromIdInputString.equals("") || fromIdInputString.equals(null)) {
            Toast.makeText(this, "Add a From ID Number", Toast.LENGTH_SHORT).show();
            return;
        }
        //Require a To ID number
        if(toIdInputString.equals("") || toIdInputString.equals(null)) {
            Toast.makeText(this, "Add a To ID Number", Toast.LENGTH_SHORT).show();
            return;
        }

        //Try to parse the ID numbers
        try {
            byte fromId = Byte.decode(fromIdInput.getText().toString());
            byte toId = Byte.decode(toIdInput.getText().toString());
        } catch (NumberFormatException e) {
            //Data is not parsable
            Toast.makeText(this, "Invalid To or From ID", Toast.LENGTH_SHORT).show();
            return;
        }

        //Split the string at commas
        String[] byteStrings = dataInputString.split(",");

        //Validate that the user data is valid
        //#TODO actually validate the string

        if(byteStrings.length != 0 && !(byteStrings.length==1 && byteStrings[0].equals(""))) {
            //Check that every split string has some characters in it
            for (String testString : byteStrings) {
                if (testString.length() == 0) {
                    Toast.makeText(this, "Invalid packet data.  Length: "+byteStrings.length, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } else {
            //No data, set the byteStrings to an empty array
            byteStrings = new String[0];
        }

        byte[] bytes = new byte[byteStrings.length];

        //Try to parse the individual strings as bytes
        try {
            for (int i = 0, n = byteStrings.length; i < n; i++) {
                //#TODO may need to subtract value from 0xFF
                bytes[i] = Integer.decode(byteStrings[i]).byteValue();
            }
        } catch (NumberFormatException e) {
            //Item was not parsable
            Toast.makeText(this, "Could not parse extra data", Toast.LENGTH_SHORT).show();
            return;
        }

        /* //Debugging to display the contents of the byte array
        String bytedata = "{";
        for(byte data : bytes) {
            bytedata += String.format("%x, ", data);
        }
        bytedata += "}";
        Toast.makeText(this, "Byte Data: " + bytedata, Toast.LENGTH_SHORT).show();
        */

        //Send the data!!!
        new SendMessageRunnable(Blos, , ));
    }

}
