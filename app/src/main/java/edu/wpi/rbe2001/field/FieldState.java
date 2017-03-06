package edu.wpi.rbe2001.field;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by jordanbrobots on 10/24/15.
 */
public class FieldState {
    private static FieldState instance = null;
    private Byte storageSupplyByte;
    private List<FieldStateChangeInterface> stateChangeListeners;

    protected FieldState() {
        storageSupplyByte = 0;
        stateChangeListeners = new ArrayList<FieldStateChangeInterface>();
    }

    /**
     * Get an instance of this class
     * @return The singleton instance of this class
     */
    public static FieldState getInstance() {
        if(instance == null) {
            instance = new FieldState();
        }
        return instance;
    }

    /**
     * Returns the bitfield of the storage and supply availability
     * @return the 8-bit bitfield for each field sensor
     */
    public Byte getStorageSupplyByte() {
        synchronized (this) {
            return storageSupplyByte;
        }
    }

    /**
     * Sets the bitfield of the storage and supply availability
     * @param data 8-bit bitfield of the storage/supply availability
     */
    public void setStorageSupplyByte(Byte data) {
        synchronized (this) {
            storageSupplyByte = data;
        }
        for(FieldStateChangeInterface listener : stateChangeListeners) {
            listener.onFieldStateChange(data);
        }
    }

    /**
     * Registers the object to listen for when the field state changes.
     * @param object object to register a listener for
     */
    public void registerFieldStateChangeListener(FieldStateChangeInterface object) {
        stateChangeListeners.add(object);
    }

    //#TODO add functionality to deregister listeners
}
