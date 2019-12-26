package com.rocago.nuojia.ble.bluetooth;


import android.bluetooth.BluetoothDevice;

import com.rocago.nuojia.ble.fastble.data.BleDevice;

/**
 * Created by zlc on 2018/12/25
 * Email: zheng.lecheng@eflagcomm.com
 * Desc: 蓝牙服务回调类
 */
public interface IBleCallBack {
    /**
     * 蓝牙发送通知回调
     *
     * @param success
     */
    default void onBleNotification(boolean success) {
    }

    /**
     * 蓝牙扫描回调
     *
     * @param device
     * @param success
     */
    void onBleScan(BleDevice device, boolean success);

    /**
     * 蓝牙连接回调
     *
     * @param device
     * @param success
     */
    void onBleConnect(BleDevice device, boolean success);

    /**
     * 蓝牙读写回调
     *
     * @param action
     * @param bytes
     * @param success
     */
    void onBleReadAndWrite(String action, byte[] bytes, boolean success);

    /**
     * 蓝牙数据改变回调
     *
     * @param bytes
     */
    void onBleDataChanged(byte[] bytes);

    /**
     * 断开连接
     */
    default void onBleDisconnect(BluetoothDevice device) {

    }
}
