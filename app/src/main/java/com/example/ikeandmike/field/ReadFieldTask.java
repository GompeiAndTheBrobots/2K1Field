package com.example.ikeandmike.field;


import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

/**
 * Created by peter on 10/24/15.
 */
public class ReadFieldTask extends AsyncTask<Void, Void, Void> {

    BluetoothSocket socket;

    public ReadFieldTask(){
    }

    protected Void doInBackground(Void ...voids) {
        //send the data!
        return null;
    }

    protected void onProgressUpdate(){

    }

    protected void onPostExecute(Void v) {
    }
}
