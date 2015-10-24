package com.example.ikeandmike.field;

import java.io.InputStream;
import java.io.OutputStream;

interface BluetoothCallback {
    void successfulConnect();
    void failedConnect();
}