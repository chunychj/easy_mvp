package com.rocago.nuojia.ble.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by zlc on 2018/12/26
 * Email: zheng.lecheng@eflagcomm.com
 * Desc: 蓝牙读写info
 */
public class BleReadWriteInfo {

    public int status;
    public String action;
    public BluetoothGattCharacteristic mCharacteristic;
    public BleReadWriteInfo(BluetoothGattCharacteristic characteristic, String action, int status){
        this.mCharacteristic = characteristic;
        this.action = action;
        this.status = status;
    }
}
