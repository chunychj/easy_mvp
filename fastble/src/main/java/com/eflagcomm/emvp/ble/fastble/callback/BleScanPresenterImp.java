package com.eflagcomm.emvp.ble.fastble.callback;

import com.eflagcomm.emvp.ble.fastble.data.BleDevice;

public interface BleScanPresenterImp {

    default void onScanStarted(boolean success){}

    void onScanning(BleDevice bleDevice);

}
