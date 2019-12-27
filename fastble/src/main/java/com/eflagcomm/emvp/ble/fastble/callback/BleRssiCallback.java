package com.eflagcomm.emvp.ble.fastble.callback;


import com.eflagcomm.emvp.ble.fastble.exception.BleException;

public abstract class BleRssiCallback extends BleBaseCallback {

    public abstract void onRssiFailure(BleException exception);

    public abstract void onRssiSuccess(int rssi);

}