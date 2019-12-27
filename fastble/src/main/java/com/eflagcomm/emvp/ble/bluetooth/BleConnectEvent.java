package com.eflagcomm.emvp.ble.bluetooth;

/**
 * @author zlc
 * email : zlc921022@163.com
 * desc :
 */
public class BleConnectEvent {
    private boolean isSuccess;

    public BleConnectEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
