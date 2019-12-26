package com.rocago.nuojia.ble.fastble.callback;


import com.rocago.nuojia.ble.fastble.exception.BleException;

public abstract class BleNotifyCallback extends BleBaseCallback {

    public abstract void onNotifySuccess();

    public void onNotifyFailure(BleException exception) {
    }

    public abstract void onCharacteristicChanged(byte[] data);

}
