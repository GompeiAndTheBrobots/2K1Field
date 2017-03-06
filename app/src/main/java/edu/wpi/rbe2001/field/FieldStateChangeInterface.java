package edu.wpi.rbe2001.field;

/**
 * Interface for objects that want events to happen when the field state
 * changes
 * Created by jordanbrobots on 10/24/15.
 */
public interface FieldStateChangeInterface {
    void onFieldStateChange(Byte supplyStorageByte);
}
