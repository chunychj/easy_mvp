package com.rocago.nuojia.ble.fastble.callback;


import com.rocago.nuojia.ble.fastble.exception.BleException;

public abstract class BleMtuChangedCallback extends BleBaseCallback {

    public abstract void onSetMTUFailure(BleException exception);

    public abstract void onMtuChanged(int mtu);

}
