package com.rocago.nuojia.ble.fastble.callback;

import com.rocago.nuojia.ble.fastble.data.BleDevice;

public interface BleScanPresenterImp {

    default void onScanStarted(boolean success){}

    void onScanning(BleDevice bleDevice);

}
