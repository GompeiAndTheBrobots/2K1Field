package com.example.ikeandmike.field;

interface BluetoothMessageCallback {
    void validMessage(BTProtocol.Type type, byte[] data);
    void invalidMessage();
}