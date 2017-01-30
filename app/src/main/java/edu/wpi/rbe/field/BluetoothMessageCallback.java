package edu.wpi.rbe.field;

interface BluetoothMessageCallback {
    void validMessage(BTProtocol.Type type, byte[] data);
    void invalidMessage();
    void robotDisconnected();
}