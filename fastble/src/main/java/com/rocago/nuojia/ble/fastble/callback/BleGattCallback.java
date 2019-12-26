
package com.rocago.nuojia.ble.fastble.callback;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.os.Build;

import com.rocago.nuojia.ble.fastble.data.BleDevice;
import com.rocago.nuojia.ble.fastble.exception.BleException;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class BleGattCallback extends BluetoothGattCallback {

    public  void onStartConnect(){}

    public abstract void onConnectFail(BleDevice bleDevice, BleException exception);

    public abstract void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status);

    public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status){

    }

}