package com.example.ikeandmike.field;

/**
 * Created by jordanbrobots on 10/24/15.
 */
public class FieldState {
    private static FieldState instance = null;
    private Byte storageSupplyByte;

    protected FieldState() {
        storageSupplyByte = 0;
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
    }


}
