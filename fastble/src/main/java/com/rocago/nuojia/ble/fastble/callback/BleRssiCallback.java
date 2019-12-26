package com.rocago.nuojia.ble.fastble.callback;


import com.rocago.nuojia.ble.fastble.exception.BleException;

public abstract class BleRssiCallback extends BleBaseCallback {

    public abstract void onRssiFailure(BleException exception);

    public abstract void onRssiSuccess(int rssi);

}