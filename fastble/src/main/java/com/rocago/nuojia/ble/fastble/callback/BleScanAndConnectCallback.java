package com.rocago.nuojia.ble.fastble.callback;


import com.rocago.nuojia.ble.fastble.data.BleDevice;

public abstract class BleScanAndConnectCallback extends BleGattCallback implements BleScanPresenterImp {

    public abstract void onScanFinished(BleDevice scanResult);

    public void onLeScan(BleDevice bleDevice) {
    }

}
