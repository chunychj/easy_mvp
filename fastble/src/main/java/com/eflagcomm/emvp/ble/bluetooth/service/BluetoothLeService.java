
package com.eflagcomm.emvp.ble.bluetooth.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.eflagcomm.emvp.base.utils.LogUtil;
import com.eflagcomm.emvp.base.utils.NumberUtil;
import com.eflagcomm.emvp.ble.bluetooth.BleReadWriteInfo;
import com.eflagcomm.emvp.ble.bluetooth.IBleCallBack;
import com.eflagcomm.emvp.ble.fastble.data.BleDevice;

import java.util.List;
import java.util.UUID;

/**
 * Created by zlc on 2018/12/13
 * Email: zheng.lecheng@eflagcomm.com
 * Desc: 蓝牙服务类
 */
@TargetApi(18)
@SuppressWarnings("all")
public class BluetoothLeService extends Service {
    //写描述的UUID
    public static final UUID DESCRIPTOR_UUID = null;//UUID.fromString(UUIDConstants.DESCRIPTOR_UUID);
    //扫描
    public static final int MSG_SCAN = 0;
    //连接成功 尚未获取服务
    public static final int MSG_CONNECT_SERVICE = 1;
    //连接成功 成功获取服务对象
    public static final int MSG_CONNECT_CONNECT = 2;
    //连接断开
    public static final int MSG_CONNECT_DISCONNECT = 3;
    //蓝牙数据改变回调
    public static final int MSG_DATA_CHANGE = 4;
    //蓝牙读写通知回调
    public static final int MSG_READ_WRITE = 5;
    //蓝牙通知写入回调
    private static final int MSG_NOTIFY_WRITE = 6;
    //蓝牙相关
    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothGatt mBluetoothGatt = null;
    private volatile boolean mDeviceBusy = false;
    //记录设备地址
    private String mDeviceAddress;
    //创建binder对象
    private final IBinder mIBinder = new LocalBinder();
    //扫描handler
    private final MainHandler sMainHandler = new MainHandler(Looper.getMainLooper());
    //蓝牙服务回调
    private IBleCallBack mBleCallBack;
    //是不是在扫描
    private volatile boolean isScan = true;
    /**
     * 设备名称
     */
    private String mDeviceName;

    //设置蓝牙服务回调
    public void setBleCallBack(IBleCallBack bleCallBack, String deviceName) {
        this.mBleCallBack = bleCallBack;
        this.mDeviceName = deviceName;
    }

    //蓝牙回调类
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        //连接回调
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (mBluetoothGatt == null) {
                return;
            }
            switch (newState) {
                case BluetoothGatt.STATE_CONNECTED:
                    sendMessage(MSG_CONNECT_SERVICE, mBluetoothGatt.getDevice());
                    break;
                case BluetoothGatt.STATE_DISCONNECTED:
                    sendMessage(MSG_CONNECT_DISCONNECT, mBluetoothGatt.getDevice());
                    break;
            }
        }

        //服务发现回调
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                sendMessage(MSG_CONNECT_CONNECT, 0, gatt.getDevice());
            } else {
                sendMessage(MSG_CONNECT_CONNECT, -1, gatt.getDevice());
            }
        }

        //数据改变回调
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic) {
            if (characteristic == null) {
                return;
            }
            byte[] bytes = characteristic.getValue();
            LogUtil.e("原始数据", NumberUtil.bytesToHexString(bytes));
            if (mBleCallBack != null) {
                mBleCallBack.onBleDataChanged(bytes);
            }
        }

        //读取数据回调
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status) {
            if (characteristic == null) {
                return;
            }
            status = (status == BluetoothGatt.GATT_SUCCESS) ? 0 : -1;
            BleReadWriteInfo info = new BleReadWriteInfo(characteristic, "read", status);
            sendMessage(MSG_READ_WRITE, info);
        }

        //写入数据回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status) {
            BluetoothLeService.this.mDeviceBusy = false;
            if (mBleCallBack != null && characteristic != null) {
                boolean success = (status == BluetoothGatt.GATT_SUCCESS);
                mBleCallBack.onBleReadAndWrite("write", characteristic.getValue(), success);
            } else {
                mBleCallBack.onBleReadAndWrite("write", characteristic.getValue(), false);
            }
        }

        //通知描述符读取回调
        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            mDeviceBusy = false;
        }

        //通知描述符写入回调
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            status = (status == BluetoothGatt.GATT_SUCCESS) ? 0 : -1;
            sendMessage(MSG_NOTIFY_WRITE, status, gatt.getDevice());
        }
    };

    /**
     * 发送消息
     *
     * @param msg
     */
    private void sendMessage(int what, Object obj) {
        sendMessageDelayed(what, obj, 50);
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    private void sendMessageDelayed(int what, Object obj, long delay) {
        Message msg = sMainHandler.obtainMessage();
        msg.what = what;
        msg.obj = obj;
        this.mDeviceBusy = false;
        sMainHandler.sendMessageDelayed(msg, delay);
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    private void sendMessage(int what, int arg1, Object obj) {
        Message msg = sMainHandler.obtainMessage();
        msg.what = what;
        msg.obj = obj;
        msg.arg1 = arg1;
        this.mDeviceBusy = false;
        sMainHandler.sendMessageDelayed(msg, 50);
    }

    //蓝牙扫描回调
    private final LeScanCallback mScanCallback = new LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device != null && TextUtils.equals(device.getName(), mDeviceName)) {
                sendMessageDelayed(MSG_SCAN, device, 0);
            }
        }
    };

    public BluetoothLeService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent var1) {
        return this.mIBinder;
    }

    @Override
    public boolean onUnbind(Intent var1) {
//        this.disconnect();
        return super.onUnbind(var1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.close();
            this.mBluetoothGatt = null;
        }
        if (this.mBleCallBack != null) {
            this.mBleCallBack = null;
        }
    }


    public boolean checkGatt() {
        if (this.mBluetoothAdapter == null) {
            Log.e("蓝牙服务", "BluetoothAdapter not initialized");
            return false;
        } else if (this.mBluetoothGatt == null) {
            Log.e("蓝牙服务", "BluetoothGatt not initialized");
            return false;
        } else if (this.mDeviceBusy) {
            Log.e("蓝牙服务", "LeService busy");
            return false;
        } else {
            return true;
        }
    }

    /**
     * 蓝牙初始化
     *
     * @return
     */
    public boolean initialize() {
        if (this.mBluetoothManager == null) {
            this.mBluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
            if (this.mBluetoothManager == null) {
                Log.e("蓝牙服务", "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        if (this.mBluetoothAdapter == null) {
            Log.e("蓝牙服务", "Unable to obtain sendBroadCast BluetoothAdapter.");
            return false;
        }
        return true;
    }


    /**
     * 从设备中读取数据
     *
     * @param characteristic
     */
    public synchronized void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (this.checkGatt() && characteristic != null) {
            this.mDeviceBusy = true;
            this.mBluetoothGatt.readCharacteristic(characteristic);
        }
    }

    /**
     * 往设备中写入数据
     *
     * @param characteristic
     * @param bytes
     * @return
     */
    public synchronized boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] bytes) {
        if (characteristic == null || bytes == null) {
            return false;
        }
        this.mDeviceBusy = true;
        characteristic.setValue(bytes);
        return this.mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, boolean b) {
        return writeCharacteristic(characteristic, new byte[]{(byte) (b ? 1 : 0)});
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, String value) {
        if (characteristic == null || value == null) {
            return false;
        }
        return writeCharacteristic(characteristic, value.getBytes());
    }

    /**
     * 获取蓝牙支持的所有服务
     *
     * @return
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        return this.mBluetoothGatt == null ? null : this.mBluetoothGatt.getServices();
    }

    /**
     * 发送一个通知
     *
     * @param characteristic
     * @param enable
     * @return
     */
    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable) {
        if (!this.checkGatt()) {
            return false;
        } else if (characteristic == null) {
            return false;
        } else if (!this.mBluetoothGatt.setCharacteristicNotification(characteristic, enable)) {
            return false;
        } else {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DESCRIPTOR_UUID);
            if (descriptor == null) {
                return false;
            } else {
                if (enable) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                } else {
                    descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                }
                this.mDeviceBusy = true;
                return this.mBluetoothGatt.writeDescriptor(descriptor);
            }
        }
    }

    /**
     * 通知是否能用
     *
     * @param characteristic
     * @return
     */
    public boolean isNotificationEnabled(BluetoothGattCharacteristic characteristic) {
        if (!this.checkGatt()) {
            return false;
        } else {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DESCRIPTOR_UUID);
            if (descriptor == null) {
                return false;
            } else {
                return descriptor.getValue() == BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
            }
        }
    }

    /**
     * 蓝牙是否能用
     *
     * @return
     */
    public boolean isBleEnable() {
        return mBluetoothAdapter != null && mBluetoothAdapter.enable();
    }

    /**
     * 开始扫描
     *
     * @param scanTime
     */
    public void scan(long scanTime) {
        Log.d("蓝牙服务", "=====开始扫描====");
        if (this.mBluetoothAdapter != null) {
            this.mBluetoothAdapter.startLeScan(this.mScanCallback);
            isScan = true;
            //超出扫描时间则停止扫描
            sMainHandler.postDelayed(mScanRunnable, scanTime);
        }
    }

    /**
     * 扫描的runnable
     */
    final Runnable mScanRunnable = new Runnable() {
        @Override
        public void run() {
            if (isScan && mBleCallBack != null) {
                stopScan();
                mBleCallBack.onBleScan(new BleDevice(null), false);
            }
        }
    };

    /**
     * 停止扫描
     */
    public void stopScan() {
        Log.e("蓝牙服务", "=====结束扫描====");
        if (this.mBluetoothAdapter != null) {
            isScan = false;
            sMainHandler.removeCallbacks(mScanRunnable);
            this.mBluetoothAdapter.stopLeScan(this.mScanCallback);
        }
    }

    /**
     * 蓝牙连接
     *
     * @param address
     * @return
     */
    public synchronized boolean connect(String address) {
        if (this.mBluetoothAdapter == null || TextUtils.isEmpty(address)) {
            Log.e("蓝牙服务", "BluetoothAdapter没有初始化 or 地址为空");
            return false;
        } else {
            BluetoothDevice device = this.mBluetoothAdapter.getRemoteDevice(address);
            return this.connect(device);
        }
    }

    /**
     * 蓝牙连接
     *
     * @param device
     * @return
     */
    public synchronized boolean connect(BluetoothDevice device) {
        if (this.mBluetoothAdapter == null || device == null) {
            Log.e("蓝牙服务", "BluetoothAdapter没有初始化 or 设备对象为null");
            return false;
        } else {
            int state = this.mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
            if (this.mDeviceAddress != null && device.getAddress().equals(this.mDeviceAddress)
                    && this.mBluetoothGatt != null) {
                return this.mBluetoothGatt.connect();
            } else {
                this.mBluetoothGatt = device.connectGatt(this, false, this.mGattCallback);
                if (Build.VERSION.SDK_INT >= 21 && this.mBluetoothGatt != null) {
                    this.mBluetoothGatt.requestConnectionPriority(1);
                }
                this.mDeviceAddress = device.getAddress();
                return true;
            }
        }
    }

    /**
     * 断开连接
     *
     * @param address
     */
    public void disconnect(String address) {
        if (this.mBluetoothAdapter == null) {
            Log.e("蓝牙服务 disconnect", "BluetoothAdapter 没有初始化");
        } else {
            BluetoothDevice device = this.mBluetoothAdapter.getRemoteDevice(address);
            int state = this.mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
            if (this.mBluetoothGatt != null && state != BluetoothProfile.STATE_DISCONNECTED) {
                this.mBluetoothGatt.disconnect();
            }
        }
    }

    /**
     * 断开连接
     */
    public void disconnect(BluetoothDevice device) {
        int state = this.mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
        if (this.mBluetoothGatt != null && state != BluetoothProfile.STATE_DISCONNECTED) {
            this.mBluetoothGatt.disconnect();
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.disconnect();
        } else {
            this.stopScan();
        }
    }

    /**
     * 当前设备是否是连接状态
     *
     * @param device
     * @return
     */
    public boolean isConnected(BluetoothDevice device) {
        if (mBluetoothManager != null && device != null) {
            int state = this.mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
            return state == BluetoothProfile.STATE_CONNECTED;
        }
        return false;
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.close();
            this.mBluetoothGatt = null;
        }
    }

    /**
     * 获取上一个连接的设备地址
     *
     * @return
     */
    public String getDeviceAddress() {
        return mDeviceAddress;
    }

    /**
     * 获取蓝牙操作对象
     *
     * @return
     */
    public BluetoothGatt getBluetoothGatt() {
        return this.mBluetoothGatt;
    }

    public BluetoothManager getBluetoothManager() {
        return mBluetoothManager;
    }

    /**
     * 定义binder类 获取蓝牙服务对象
     */
    public final class LocalBinder extends Binder {
        public LocalBinder() {

        }

        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    /**
     * 蓝牙操作handler
     */
    private final class MainHandler extends Handler {

        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mBleCallBack == null) {
                return;
            }
            switch (msg.what) {
                case MSG_SCAN:
                    isScan = false;
                    sMainHandler.removeCallbacks(mScanRunnable);
                    mBleCallBack.onBleScan(new BleDevice((BluetoothDevice) msg.obj), true);
                    break;
                case MSG_CONNECT_SERVICE:
                    if (mBluetoothGatt != null) {
                        mBluetoothGatt.discoverServices();
                    }
                    break;
                case MSG_CONNECT_CONNECT:
                    mBleCallBack.onBleConnect(new BleDevice((BluetoothDevice) msg.obj), msg.arg1 == 0);
                    break;
                case MSG_CONNECT_DISCONNECT:
                    mBleCallBack.onBleDisconnect((BluetoothDevice) msg.obj);
                    BluetoothLeService.this.close();
                    break;
                case MSG_NOTIFY_WRITE:
                    mBleCallBack.onBleNotification(msg.arg1 == 0);
                    break;
                case MSG_DATA_CHANGE:
                    BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) msg.obj;
                    byte[] bytes = characteristic.getValue();
                    mBleCallBack.onBleDataChanged(characteristic.getValue());
                    break;
                case MSG_READ_WRITE:
                    BleReadWriteInfo info = (BleReadWriteInfo) msg.obj;
                    mBleCallBack.onBleReadAndWrite(info.action, info.mCharacteristic.getValue(), info.status == 0);
                    break;
            }
        }
    }
}
