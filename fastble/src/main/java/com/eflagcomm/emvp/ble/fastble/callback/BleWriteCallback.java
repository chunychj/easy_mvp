package com.eflagcomm.emvp.ble.fastble.callback;


import com.eflagcomm.emvp.ble.fastble.exception.BleException;

public abstract class BleWriteCallback extends BleBaseCallback {

    public abstract void onWriteSuccess(int current, int total, byte[] justWrite);

    public abstract void onWriteFailure(BleException exception);

}
