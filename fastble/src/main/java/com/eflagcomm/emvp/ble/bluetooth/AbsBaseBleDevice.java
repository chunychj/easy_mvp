package com.eflagcomm.emvp.ble.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;

import com.eflagcomm.emvp.base.BaseApplication;
import com.eflagcomm.emvp.base.utils.LogUtil;
import com.eflagcomm.emvp.base.utils.NumberUtil;
import com.eflagcomm.emvp.base.utils.SPUtil;
import com.eflagcomm.emvp.ble.bluetooth.constant.DeviceConstants;
import com.eflagcomm.emvp.ble.bluetooth.constant.UUIDConstants;
import com.eflagcomm.emvp.ble.bluetooth.service.BluetoothLeService;
import com.eflagcomm.emvp.ble.fastble.BleManager;
import com.eflagcomm.emvp.ble.fastble.callback.BleGattCallback;
import com.eflagcomm.emvp.ble.fastble.callback.BleNotifyCallback;
import com.eflagcomm.emvp.ble.fastble.callback.BleReadCallback;
import com.eflagcomm.emvp.ble.fastble.callback.BleScanCallback;
import com.eflagcomm.emvp.ble.fastble.callback.BleWriteCallback;
import com.eflagcomm.emvp.ble.fastble.data.BleDevice;
import com.eflagcomm.emvp.ble.fastble.exception.BleException;
import com.eflagcomm.emvp.ble.fastble.scan.BleScanRuleConfig;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zlc on 2018/12/13
 * Email: zheng.lecheng@eflagcomm.com
 * Desc: 设备抽象类
 */
@TargetApi(18)
@SuppressWarnings("all")
public abstract class AbsBaseBleDevice implements IBleDeviceWriteBytes {

    //服务UUID
    protected static String SERVICE_UUID = UUIDConstants.SERVICE_UUID;
    //通知UUID
    protected static String NOTIFY_CHAR_UUID = UUIDConstants.NOTIFY_CHAR_UUID;
    //读UUID
    protected static String READ_CHAR_UUID = UUIDConstants.READ_CHAR_UUID;
    //写UUID
    protected static String WRITE_CHAR_UUID = UUIDConstants.WRITE_CHAR_UUID;
    //默认扫描时间 10秒
    protected static final long SCAN_TIME_OUT = 10 * 1000;
    //开启指令名称
    protected String START_CMD;
    //蓝牙服务
    protected BluetoothLeService mService;
    //蓝牙设备
    protected BluetoothDevice mDevice;
    //设备对象
    private BleDevice mBleDevice;
    //TAG
    private final String TAG = "AbsBaseBleDevice";
    //蓝牙服务回调
    private IBleCallBack mBleCallBack;
    //切换蓝牙调用方式
    private static final int type = DeviceConstants.SERVICE;
    //已连接过的设备列表
    private final ConcurrentHashMap<String, BluetoothDevice> mDeviceMap;
    //fastble上次扫描的设备
    private BleDevice mScanedBledevice;
    //蓝牙Manager
    private final BluetoothManager mBluetoothManager;
    //蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    /**
     * 指令直接间隔发送时间
     */
    private final static long SLEEP_TIME = 100;

    protected AbsBaseBleDevice() {
        mDeviceMap = new ConcurrentHashMap<>();
        mBluetoothManager = (BluetoothManager) BaseApplication.getApplication().
                getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager != null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }
    }

    /**
     * 设置蓝牙服务回调
     * 必须设置
     */
    public void setBleCallBack(IBleCallBack bleCallBack) {
        if (type == DeviceConstants.SERVICE && mService != null) {
            mService.setBleCallBack(bleCallBack,getDeviceName());
        } else if (type == DeviceConstants.FASTBLE) {
            this.mBleCallBack = bleCallBack;
        }
    }

    /**
     * 设置蓝牙服务
     *
     * @param service
     */
    public void setService(BluetoothLeService service) {
        this.mService = service;
    }

    /**
     * 获取服务对象
     *
     * @return
     */
    public BluetoothLeService getService() {
        return mService;
    }

    /**
     * fastble设置上次连接的设备，方便下次直接连接
     *
     * @param scanedBledevice
     */
    public void setScanedBledevice(BleDevice scanedBledevice) {
        mScanedBledevice = scanedBledevice;
    }

    /**
     * 开始扫描
     */
    public void startScan() {
        SystemClock.sleep(SLEEP_TIME);
        if (type == DeviceConstants.SERVICE && mService != null) {
            mService.scan(SCAN_TIME_OUT);
        } else if (type == DeviceConstants.FASTBLE) {
            fastBleScan();
        }
    }

    /**
     * fastble库扫描
     */
    private void fastBleScan() {
        fastBleScan(SCAN_TIME_OUT);
    }

    private void fastBleScan(long timeOut) {
        BleManager.getInstance().initScanRule(getScanRuleConfig(SCAN_TIME_OUT))
                .scan(new BleScanCallback() {
                    @Override
                    public void onScanning(BleDevice bleDevice) {
                        if (TextUtils.equals(getDeviceName(), bleDevice.getName()) && mBleCallBack != null) {
                            mBleCallBack.onBleScan(bleDevice, true);
                        }
                    }
                });
    }

    /**
     * 开始扫描
     */
    public void startScan(long timeOut) {
        timeOut = timeOut > SCAN_TIME_OUT ? SCAN_TIME_OUT : timeOut;
        if (type == DeviceConstants.SERVICE && mService != null) {
            mService.scan(timeOut);
        } else if (type == DeviceConstants.FASTBLE) {
            fastBleScan(timeOut);
        }
    }

    /**
     * 初始化扫描规则
     */
    private BleScanRuleConfig getScanRuleConfig(long timeOut) {
        return new BleScanRuleConfig.Builder()
                .setServiceUuids(new UUID[]{UUID.fromString(SERVICE_UUID)})
                .setDeviceName(true, getDeviceName())
                .setScanTimeOut(timeOut)
                .build();
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        if (type == DeviceConstants.SERVICE && mService != null) {
            mService.stopScan();
        } else if (type == DeviceConstants.FASTBLE) {
            BleManager.getInstance().cancelScan();
        }
    }

    /**
     * 连接蓝牙
     *
     * @param device
     */
    public void connect(BleDevice device) {
        if (device == null) {
            throw new NullPointerException("device is not null!");
        }
        this.mBleDevice = device;
        this.mDevice = device.getDevice();
        if (type == DeviceConstants.SERVICE && mService != null && mDevice != null) {
            mService.connect(mDevice.getAddress());
        } else if (type == DeviceConstants.FASTBLE) {
            fastbleConnect(mBleDevice);
        }
    }

    /**
     * fastble库连接
     */
    private void fastbleConnect(BleDevice bleDevice) {
        if (mBleCallBack == null || bleDevice == null) {
            return;
        }
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                mBleCallBack.onBleConnect(bleDevice, false);
                LogUtil.e(TAG, "onConnectFail error: " + exception.getDescription());
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                mBleCallBack.onBleConnect(bleDevice, true);
                LogUtil.e(TAG, "onConnectSuccess 成功= " + (status == BluetoothGatt.GATT_SUCCESS));
            }
        });
    }

    /**
     * 连接已连接过设备
     */
    public void connect() {
        SystemClock.sleep(SLEEP_TIME);
        if (type == DeviceConstants.SERVICE && mService != null) {
            mService.connect(mService.getDeviceAddress());
        } else if (type == DeviceConstants.FASTBLE) {
            fastbleConnect(mScanedBledevice);
        }
    }

    /**
     * 通知是否能用
     *
     * @return
     */
    public boolean isNotificationEnabled() {
        return mService != null && mService.isNotificationEnabled(getCharacteristic(NOTIFY_CHAR_UUID));
    }

    /**
     * 发送通知
     */
    public void enableNotification() {
        if (type == DeviceConstants.SERVICE && mService != null) {
            BluetoothGattCharacteristic characteristic = getCharacteristic(NOTIFY_CHAR_UUID);
            mService.setCharacteristicNotification(characteristic, true);
        } else if (type == DeviceConstants.FASTBLE && mBleDevice != null) {
            fastbleEnableNotification();
        }
    }

    /**
     * fastble发送通知
     */
    private void fastbleEnableNotification() {
        if (mBleCallBack == null) {
            return;
        }
        SystemClock.sleep(SLEEP_TIME);
        BleManager.getInstance().notify(mBleDevice, SERVICE_UUID, NOTIFY_CHAR_UUID,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        mBleCallBack.onBleNotification(true);
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        mBleCallBack.onBleDataChanged(data);
                    }
                });
    }

    /**
     * 取消通知
     */
    public void disableNotification() {
        if (type == DeviceConstants.SERVICE && mService != null) {
            BluetoothGattCharacteristic characteristic = getCharacteristic(NOTIFY_CHAR_UUID);
            mService.setCharacteristicNotification(characteristic, false);
        } else if (type == DeviceConstants.FASTBLE && mBleDevice != null) {
            BleManager.getInstance().stopNotify(mBleDevice, SERVICE_UUID, NOTIFY_CHAR_UUID);
        }
    }

    /**
     * 获取读写的特征值
     *
     * @param uuidString
     * @return
     */
    private BluetoothGattCharacteristic getCharacteristic(String uuidString) {
        BluetoothGattService gattService = getGattService();
        if (gattService != null && !TextUtils.isEmpty(uuidString)) {
            return gattService.getCharacteristic(UUID.fromString(uuidString));
        }
        return null;
    }

    /**
     * 获取特定UUID对应的服务
     *
     * @return
     */
    private BluetoothGattService getGattService() {
        if (mService != null) {
            List<BluetoothGattService> gattServices = mService.getSupportedGattServices();
            if (gattServices == null){
                return null;
            }
            for (BluetoothGattService service : gattServices) {
                if (service.getUuid().equals(UUID.fromString(SERVICE_UUID))) {
                    return service;
                }
            }
        }
        return null;
    }

    /**
     * 从设备中读取数据
     */
    protected void readData() {
        if (type == DeviceConstants.SERVICE && mService != null) {
            SystemClock.sleep(SLEEP_TIME);
            mService.readCharacteristic(getCharacteristic(READ_CHAR_UUID));
        } else if (type == DeviceConstants.FASTBLE && mBleDevice != null) {
            fastbleReadData();
        }
    }

    /**
     * fastble读取数据
     */
    private void fastbleReadData() {
        if (mBleCallBack == null) {
            return;
        }
        SystemClock.sleep(SLEEP_TIME);
        BleManager.getInstance().read(mBleDevice, SERVICE_UUID, READ_CHAR_UUID,
                new BleReadCallback() {
                    @Override
                    public void onReadSuccess(byte[] data) {
                        mBleCallBack.onBleReadAndWrite("read", data, true);
                    }

                    @Override
                    public void onReadFailure(BleException exception) {
                        mBleCallBack.onBleReadAndWrite("read", null, false);
                    }
                });
    }

    /**
     * 按下时写入字节数组数据 针对按摩仪
     *
     * @param cmd
     */
    public void writeDownBytes(String cmd) {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                SystemClock.sleep(SLEEP_TIME);
                if (type == DeviceConstants.SERVICE && mService != null) {
                    BluetoothGattCharacteristic characteristic = getCharacteristic(WRITE_CHAR_UUID);
                    byte[] bytes = getDownBytes(cmd);
                    mService.writeCharacteristic(characteristic, bytes);
                } else if (type == DeviceConstants.FASTBLE && mBleDevice != null) {
                    byte[] bytes = getDownBytes(cmd);
                    writeBytesData(bytes);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(s -> {
            LogUtil.e(TAG, "指令发送成功");
        }, throwable -> {
            LogUtil.e(TAG, "指令发送失败 : error = " + throwable.getMessage());
        });
    }

    /**
     * 抬起时写入字节数组数据 针对按摩仪
     *
     * @param cmd
     */
    public void writeUpBytes(String cmd) {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                SystemClock.sleep(SLEEP_TIME);
                if (type == DeviceConstants.SERVICE && mService != null) {
                    BluetoothGattCharacteristic characteristic = getCharacteristic(WRITE_CHAR_UUID);
                    byte[] bytes = getUpBytes(cmd);
                    mService.writeCharacteristic(characteristic, bytes);
                } else if (type == DeviceConstants.FASTBLE && mBleDevice != null) {
                    byte[] bytes = getUpBytes(cmd);
                    writeBytesData(bytes);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(s -> {
            LogUtil.e(TAG, "指令发送成功");
        }, throwable -> {
            LogUtil.e(TAG, "指令发送失败 : error = " + throwable.getMessage());
        });
    }

    /**
     * 往设备中写入字符串数据
     *
     * @param cmd 0 开启功能   1 关闭功能
     */
    public void writeStrData(int cmd) {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                try {
                    SystemClock.sleep(SLEEP_TIME);
                    if (type == DeviceConstants.SERVICE && mService != null) {
                        BluetoothGattCharacteristic characteristic = getCharacteristic(WRITE_CHAR_UUID);
                        String data = getWriteString(cmd);
                        mService.writeCharacteristic(characteristic, data.getBytes());
                    } else if (type == DeviceConstants.FASTBLE && mBleDevice != null) {
                        String data = getWriteString(cmd);
                        writeBytesData(data.getBytes());
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                    e.printStackTrace();
                    LogUtil.e(TAG, "writeStrData error: " + e.getMessage());
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(s -> {
            LogUtil.e(TAG, "指令发送成功");
        }, throwable -> {
            LogUtil.e(TAG, "指令发送失败 : error = " + throwable.getMessage());
        });
    }

    /**
     * 写入字节数组数据
     *
     * @param bytes
     */
    private void writeBytesData(byte[] bytes) {
        if (mBleCallBack == null) {
            return;
        }
        BleManager.getInstance().write(mBleDevice, SERVICE_UUID, WRITE_CHAR_UUID,
                bytes, new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] write) {
                        mBleCallBack.onBleReadAndWrite("write", write, true);
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        mBleCallBack.onBleReadAndWrite("write", null, true);
                    }
                });
    }

    /**
     * 数据读取回调方法
     *
     * @param isSuccess
     */
    public void onReaded(boolean isSuccess) {
        //LogUtil.e("device onReaded isSuccess", isSuccess + "");
    }

    /**
     * 数据写入回调方法
     *
     * @param isSuccess
     */
    public void onWrited(boolean isSuccess) {
        //LogUtil.e("device onWrited isSuccess", isSuccess + "");
    }

    /**
     * 从蓝牙设备读取过来的数据
     *
     * @param bytes
     * @return
     */
    public String getReadData(byte[] bytes) {
        return NumberUtil.bytesToHexString(bytes);
    }

    /**
     * 判断设备是否支持ble
     *
     * @return
     */
    public boolean isSupportBle() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && BaseApplication.getApplication().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * 监测蓝牙是否能用
     *
     * @return
     */
    public boolean isBlueEnable() {
        if (type == DeviceConstants.SERVICE && mBluetoothManager != null) {
            BluetoothAdapter adapter = mBluetoothManager.getAdapter();
            return adapter != null && adapter.isEnabled();
        } else if (type == DeviceConstants.FASTBLE) {
            return BleManager.getInstance().isBlueEnable();
        }
        return false;
    }

    /**
     * 打开蓝牙
     */
    public void enableBluetooth() {
        if (mBluetoothManager.getAdapter() != null) {
            mBluetoothManager.getAdapter().enable();
        }
    }

    /**
     * 判断当前设备是否是已连接状态
     *
     * @return
     */
    public boolean isConnected() {
        try {
            SystemClock.sleep(SLEEP_TIME);
            String connectedAddress = SPUtil.getConnectedAddress();
            if (TextUtils.isEmpty(connectedAddress)) {
                return false;
            } else {
                BluetoothDevice connectedDevice = mBluetoothAdapter == null ? null :
                        mBluetoothAdapter.getRemoteDevice(connectedAddress);
                if (connectedDevice == null) {
                    return isConnected(mBleDevice, mDevice);
                } else {
                    BleDevice bleDevice = new BleDevice(connectedDevice);
                    this.mBleDevice = bleDevice;
                    this.mDevice = connectedDevice;
                    this.mService = BleServiceHelper.getHelper().getBleService();
                    return isConnected(bleDevice, connectedDevice);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("absBaseBleDevice isConnected error", e.getMessage());
        }
        return false;
    }

    /**
     * 设备是否已经连接
     *
     * @param bleDevice
     * @return
     */
    private boolean isConnected(BleDevice bleDevice, BluetoothDevice device) {
        if (type == DeviceConstants.SERVICE && mService != null && device != null) {
            return mService.isConnected(device);
        } else if (type == DeviceConstants.FASTBLE && bleDevice != null) {
            return BleManager.getInstance().isConnected(bleDevice);
        }
        return false;
    }

    /**
     * 是否是已连接设备地址
     *
     * @return
     */
    public boolean isConnectedAddress() {
        if (type == DeviceConstants.SERVICE && mService != null && mDeviceMap != null) {
            String deviceAddress = mService.getDeviceAddress();
            boolean isUse = mService.isBleEnable() && !TextUtils.isEmpty(deviceAddress);
            return isUse && mDeviceMap.containsKey(deviceAddress);
        } else if (type == DeviceConstants.FASTBLE) {
            return fastbleIsConnectedAddress();
        }
        return false;
    }

    /**
     * fastble监测是否是已连接设备
     *
     * @return
     */
    private boolean fastbleIsConnectedAddress() {
        if (mScanedBledevice != null && mScanedBledevice.getDevice() != null) {
            return isBlueEnable() && mDeviceMap.containsKey(mScanedBledevice.
                    getDevice().getAddress());
        }
        return false;
    }

    /**
     * 获取已连接设备集合
     *
     * @return
     */
    public ConcurrentHashMap<String, BluetoothDevice> getDeviceMap() {
        return mDeviceMap;
    }

    /**
     * 扫描到的设备添加到集合中
     */
    public void addDevice(BleDevice bleDevice) {
        if (mDeviceMap != null && bleDevice != null) {
            this.mScanedBledevice = bleDevice;
            BluetoothDevice device = bleDevice.getDevice();
            if (device != null && !mDeviceMap.containsKey(device.getAddress())) {
                mDeviceMap.put(device.getAddress(), device);
            }
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        try {
            if (type == DeviceConstants.SERVICE && mService != null) {
                this.mService.disconnect();
            } else if (type == DeviceConstants.FASTBLE && mBleDevice != null) {
                BleManager.getInstance().disconnect(mBleDevice);
            }
//            SPUtil.clearBleDeviceAddress();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("absBaseBleDevice disconnect error", e.getMessage());
        }
    }

    /**
     * 获取需要写入的字符串
     *
     * @return
     */
    protected abstract String getWriteString(int cmd);

    /**
     * 获取设备名称
     * @return
     */
    protected abstract String getDeviceName();

    /**
     * @return 获取蓝牙适配器
     */
    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    /**
     * 释放资源
     */
    public void onDestroy() {
        if (type == DeviceConstants.SERVICE && this.mService != null) {
         //   writeStrData(CmdConstants.CMD_CLOSE);
            this.mService.onDestroy();
        } else if (type == DeviceConstants.FASTBLE) {
            BleManager.getInstance().destroy();
        }
    }
}
