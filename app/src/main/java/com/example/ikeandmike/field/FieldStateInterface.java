package com.example.ikeandmike.field;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by peter on 10/24/15.
 */
public class FieldStateInterface implements FieldStateChangeInterface, Button.OnClickListener {

    @Override
    public void onFieldStateChange(Byte supplyStorageByte) {

    }

    @Override
    public void onClick(View v) {
        Byte mask = 0;
        switch(v.getId()) {
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
