package com.eflagcomm.emvp.ble.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;

/**
 * @author zlc
 * email : zlc921022@163.com
 * desc : 蓝牙打开帮助类
 */
public class BleOpenHelper {

    private Handler mHandler = new Handler();
    private BluetoothAdapter mBluetoothAdapter;

    private BleOpenHelper() {
    }

    public static BleOpenHelper getBluetoothHelper() {
        return new BleOpenHelper();
    }

    public BleOpenHelper setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.mBluetoothAdapter = bluetoothAdapter;
        return this;
    }

    public BleOpenHelper enableBluetooth() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.enable();
            mHandler.postDelayed(mBleStatusRunnable, 1000);
        }
        return this;
    }

    /**
     * 蓝牙打开状态runnable
     */
    private final Runnable mBleStatusRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothAdapter.isEnabled()) {
                mHandler.removeCallbacks(this);
                if (mBleOpenListener != null) {
                    mBleOpenListener.onBluetoothOpen();
                }
            } else {
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    /**
     * 蓝牙打开的回调
     */
    public interface BleOpenListener {
        void onBluetoothOpen();
    }

    private BleOpenListener mBleOpenListener;

    public void setBleOpenListener(BleOpenListener bleOpenListener) {
        mBleOpenListener = bleOpenListener;
    }

    public void remove() {
        mHandler.removeCallbacks(mBleStatusRunnable);
    }
}
