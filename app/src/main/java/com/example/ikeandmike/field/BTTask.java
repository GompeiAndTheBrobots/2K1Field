package com.example.ikeandmike.field;


import android.os.AsyncTask;

/**
 * Created by peter on 10/24/15.
 */
public class BTTask extends AsyncTask<Void, Void, Void> {

    BTProtocol.Type type;
    byte data;

    public BTTask(BTProtocol.Type type, byte data){
        this.type = type;
        this.data = data;
    }

    protected Void doInBackground(Void ...voids) {
        //send the data!

        return null;
    }

    protected void onPostExecute(Void v) {
    }
}
