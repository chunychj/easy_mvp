package com.eflagcomm.emvp.ble.bluetooth.activity;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.eflagcomm.emvp.base.dialog.MyDialog;
import com.eflagcomm.emvp.base.presenter.AbsBasePresenter;
import com.eflagcomm.emvp.base.ui.AbsBaseActivity;
import com.eflagcomm.emvp.base.utils.LogUtil;
import com.eflagcomm.emvp.base.utils.SPUtil;
import com.eflagcomm.emvp.base.utils.ToastUtil;
import com.eflagcomm.emvp.ble.bluetooth.AbsBaseBleDevice;
import com.eflagcomm.emvp.ble.bluetooth.BleConnectEvent;
import com.eflagcomm.emvp.ble.bluetooth.BleOpenHelper;
import com.eflagcomm.emvp.ble.bluetooth.BleServiceHelper;
import com.eflagcomm.emvp.ble.bluetooth.IBleCallBack;
import com.eflagcomm.emvp.ble.bluetooth.constant.DeviceConstants;
import com.eflagcomm.emvp.ble.bluetooth.service.BluetoothLeService;
import com.eflagcomm.emvp.ble.fastble.data.BleDevice;

import org.greenrobot.eventbus.EventBus;

/**
 * @author zlc
 * email : zlc921022@163.com
 * desc : 蓝牙的父类
 */
public abstract class AbsBleBaseActivity<T extends AbsBasePresenter, V extends AbsBaseBleDevice> extends AbsBaseActivity<T> implements BleServiceHelper.
        OnServiceBindListener, IBleCallBack {

    protected V mDevice;
    protected Activity mActivity;
    //重连次数
    private int mConnectTimes = 0;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    protected boolean isBindService = false;
    private MyDialog mDialog;
    private BleOpenHelper mBluetoothHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mDevice = getDevice();
        super.onCreate(savedInstanceState);
        this.mActivity = this;
    }

    @Override
    protected void initData() {
        super.initData();
        if (isUseBle()) {
            mHandler.postDelayed(this::initBluetooth, 50);
        }
    }

    public void initBluetooth() {
        if (!mDevice.isBlueEnable()) {
            showBleOpenDialog();
           // ToastUtil.showShortToast(this, "请先打开蓝牙!");
        } else if (!mDevice.isSupportBle()) {
            ToastUtil.showShortToast(this, "当前手机不支持蓝牙4.0!");
        } else if (mDevice.isConnected()) {
            mDevice.setBleCallBack(this);
            writeCmdOnConnected();
            LogUtil.e(TAG, "蓝牙设备已连接");
        } else {
            connect();
        }
    }

    private void connect() {
        int type = getBleType();
        if (type == DeviceConstants.SERVICE) {
            isBindService = true;
            BleServiceHelper.getHelper().bindService(this, this);
        } else if (type == DeviceConstants.FASTBLE) {
            initFastble();
        }
    }

    /**
     * fastble蓝牙初始化
     */
    private void initFastble() {
        mDevice.setBleCallBack(this);
        if (mDevice.isConnectedAddress()) {
            mDevice.connect();
        } else {
            mDevice.startScan();
        }
    }

    /**
     * 服务绑定成功
     *
     * @param isSuccess
     */
    @Override
    public void onBind(BluetoothLeService service, boolean isSuccess) {
        if (isSuccess) {
            mDevice.setService(service);
            mDevice.setBleCallBack(this);
            if (mDevice.isConnectedAddress()) {
                mDevice.connect();
            } else {
                mDevice.startScan();
            }
        }
    }

    /**
     * 蓝牙扫描回调
     *
     * @param device
     * @param success
     */
    @Override
    public void onBleScan(BleDevice device, boolean success) {
        if (!success) {
            dismissLoadingDialog();
            ToastUtil.showShortToast(this, "设备扫描失败!");
        } else if (mDevice != null) {
            mDevice.stopScan();
            mDevice.addDevice(device);
            mDevice.connect(device);
        }
    }

    /**
     * 蓝牙连接回调
     *
     * @param device
     * @param success
     */
    @Override
    public void onBleConnect(BleDevice device, boolean success) {
        try {
            if (success) {
                SPUtil.saveBleDeviceAddress(device.getDevice());
                ToastUtil.showShortToast(this, "连接成功");
                mDevice.enableNotification();
                dismissLoadingDialog();
                EventBus.getDefault().post(new BleConnectEvent(true));
            } else if (mConnectTimes < 3) {
                mDevice.connect(device);
                mConnectTimes++;
                showReconnectLoading();
                ToastUtil.showShortToast(this, "连接失败");
            } else {
                dismissLoadingDialog();
                ToastUtil.showShortToast(this, "连接失败");
                EventBus.getDefault().post(new BleConnectEvent(false));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "蓝牙连接error : " + e.getMessage());
        }
    }

    @Override
    public void onBleDisconnect(BluetoothDevice device) {
        //断开连接发送一个消息
        EventBus.getDefault().post(new BleConnectEvent(false));
    }

    /**
     * 通知写入回调
     *
     * @param success true 成功 false 失败
     */
    @Override
    public void onBleNotification(boolean success) {

    }

    /**
     * 蓝牙设备读写回调
     *
     * @param action read 读成功 write 写入成功
     * @param bytes  写入成功返回的数据
     * @param success true 成功 false 失败
     */
    @Override
    public void onBleReadAndWrite(String action, byte[] bytes, boolean success) {
        if (mDevice == null) {
            LogUtil.e(TAG, "设备对象为空了");
        } else if (action.equals("write")) {
            LogUtil.e(TAG + " 数据写入成功", success + "");
        }
    }

    /**
     * 蓝牙设备数据改变回调  获取设备返回的数据
     *
     * @param bytes
     */
    @Override
    public void onBleDataChanged(byte[] bytes) {
        runOnUiThread(() -> getDataFromDevice(bytes));
    }


    /**
     * 获取具体要操作的设备
     *
     * @return
     */
    protected abstract V getDevice();

    /**
     * 获取设备返回的数据
     *
     * @param bytes
     */
    protected abstract void getDataFromDevice(byte[] bytes);

    @Override
    public T getPresenter() {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDevice != null && isDisconnectOnDestroy()) {
            mDevice.disconnect();
        }
        if (getBleType() == DeviceConstants.SERVICE && isBindService) {
            BleServiceHelper.getHelper().unBindService(this);
        }
        if(mBluetoothHelper != null){
            mBluetoothHelper.remove();
        }
    }

    /**
     * 切换蓝牙操作方式
     *
     * @return
     */
    protected int getBleType() {
        return DeviceConstants.SERVICE;
    }


    /**
     * 显示重连对话框
     */
    private void showReconnectLoading() {
       // todo
    }

    /**
     * 关闭loading
     */
    protected void dismissLoadingDialog() {
       // todo
    }

    /**
     * 退出页面是否断开蓝牙连接
     *
     * @return
     */
    protected boolean isDisconnectOnDestroy() {
        return false;
    }

    /**
     * 指令写入成功方法
     *
     * @param cmd 写入成功的指令名称
     */
    protected void writeCmdSuccess(String cmd) {
    }

    /**
     * 已连接情况下写入指令
     */
    protected void writeCmdOnConnected() {
    }

    /**
     * 是否开启蓝牙
     *
     * @return
     */
    protected boolean isUseBle() {
        return true;
    }

    /**
     * 数据写入失败
     *
     * @param bytes
     * @param e
     */
    protected void onWriteFailure(byte[] bytes, Exception e) {
    }

    protected void showBleOpenDialog() {
        if (mActivity != null) {
            mDialog = MyDialog.getDialog(mActivity);
            mDialog.setContent("请先打开蓝牙设备")
                    .showDialog();
            mDialog.setOnDialogBtnClickListener(() -> {
                if (mBluetoothHelper == null) {
                    mBluetoothHelper = BleOpenHelper.getBluetoothHelper();
                }
                mBluetoothHelper.setBluetoothAdapter(mDevice.getBluetoothAdapter())
                        .enableBluetooth()
                        .setBleOpenListener(this::connect);
            });
        }
    }
}
