package com.eflagcomm.emvp.ble.fastble.callback;


import com.eflagcomm.emvp.ble.fastble.exception.BleException;

public abstract class BleIndicateCallback extends BleBaseCallback {

    public abstract void onIndicateSuccess();

    public abstract void onIndicateFailure(BleException exception);

    public abstract void onCharacteristicChanged(byte[] data);
}
