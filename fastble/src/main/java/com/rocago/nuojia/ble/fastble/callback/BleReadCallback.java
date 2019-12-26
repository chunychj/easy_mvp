package com.rocago.nuojia.ble.fastble.callback;


import com.rocago.nuojia.ble.fastble.exception.BleException;

public abstract class BleReadCallback extends BleBaseCallback {

    public abstract void onReadSuccess(byte[] data);

    public abstract void onReadFailure(BleException exception);

}
