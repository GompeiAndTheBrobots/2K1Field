package edu.wpi.rbe2001.field;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.List;

public class DeviceDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private BTCommunicator comms = BTCommunicator.getInstance();
    private List<String> device_names;

    public DeviceDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedState) {
        this.device_names = comms.getDeviceNames();
        ArrayAdapter<String> deviceListAdapter;
        deviceListAdapter = new ArrayAdapter<String>(getActivity(),
                                                     android.R.layout.simple_list_item_1,
                                                     device_names);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_robot).setAdapter(deviceListAdapter, this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String selected_device_name = this.device_names.get(which);
        Log.e("Click", String.valueOf(which) + " " + selected_device_name);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.robot_name), selected_device_name);
        editor.apply();
    }
}
