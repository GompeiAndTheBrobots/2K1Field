package com.example.ikeandmike.field;

import java.io.InputStream;
import java.io.OutputStream;

interface BluetoothConnectionCallback {
    void successfulConnect();
    void failedConnect();
}