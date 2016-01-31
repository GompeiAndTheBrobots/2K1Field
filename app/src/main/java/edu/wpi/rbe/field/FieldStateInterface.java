package edu.wpi.rbe.field;

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
                mask = 1 << 4;
                break;
            case R.id.Storage2:
                mask = 1 << 5;
                break;
            case R.id.Storage3:
                mask = 1 << 6;
                break;
            case R.id.Storage4:
                mask = (byte) -128;
                break;
            case R.id.Supply1:
                mask = 1;
                break;
            case R.id.Supply2:
                mask = 1 << 1;
                break;
            case R.id.Supply3:
                mask = 1 << 2;
                break;
            case R.id.Supply4:
                mask = 1 << 3;
                break;
            default:
                break;
        }
        if (mask != 0) {
            FieldState state = FieldState.getInstance();
            Byte field = state.getStorageSupplyByte();
            Byte newField = (byte) (field ^ mask);
            state.setStorageSupplyByte(newField);
        }
    }
}
