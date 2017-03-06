package edu.wpi.rbe2001.field;

interface BluetoothMessageCallback {
    void validMessage(BTProtocol.Type type, byte[] data);
    void invalidMessage();
    void robotDisconnected();
}