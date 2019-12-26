package com.rocago.nuojia.ble.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.rocago.nuojia.ble.bluetooth.service.BluetoothLeService;

/**
 * Created by zlc on 2018/12/13
 * Email: zheng.lecheng@eflagcomm.com
 * Desc: 蓝牙服务帮助类
 */
public class BleServiceHelper {

    private BluetoothLeService mBluetoothLeService;
    private static final String TAG = "BleServiceHelper";
    private OnServiceBindListener mOnServiceBindListener;
    private boolean mIsBind;

    private BleServiceHelper() {
    }

    public static BleServiceHelper getHelper(){
        return ServiceHelperHolder.helper;
    }

    private static class ServiceHelperHolder{
        private static final BleServiceHelper helper = new BleServiceHelper();
    }

    /**
     * 开启服务
     * @param context
     */
    public void startService(Context context) {
        if(context == null){
            Log.e(TAG,"context is null");
        }else {
            Intent intent = new Intent(context, BluetoothLeService.class);
            context.startService(intent);
        }
    }

    /**
     * 停止服务
     * @param context
     */
    public void stopService(Context context) {
        if(context == null){
            Log.e(TAG,"context is null");
        }else {
            Intent intent = new Intent(context, BluetoothLeService.class);
            context.stopService(intent);
        }
    }

    /**
     * 绑定服务
     * @param context
     */
    public void bindService(Context context,OnServiceBindListener onServiceBindListener) {
        this.mOnServiceBindListener = onServiceBindListener;
        if(context == null){
            Log.e(TAG,"context is null");
        }else{
            Intent intent = new Intent(context, BluetoothLeService.class);
            mIsBind = context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * 解绑服务
     * @param context
     */
    public void unBindService(Context context) {
        if(context == null){
            Log.e(TAG,"context is null");
        }else if(mIsBind){
            mIsBind = false;
            context.unbindService(mServiceConnection);
        }
    }

    /**
     * 销毁服务
     */
    public void onDestory() {
        if (mBluetoothLeService != null) {
            mBluetoothLeService.onDestroy();
        }
    }

    /**
     * 获取蓝牙连接服务对象
     * @return
     */
    public BluetoothLeService getBleService() {
        return mBluetoothLeService;
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) iBinder).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e("BleServiceHelper", "蓝牙没有初始化");
            }
            if(mOnServiceBindListener != null){
                mOnServiceBindListener.onBind(mBluetoothLeService,true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            if(mOnServiceBindListener != null){
                mOnServiceBindListener.onBind(null,false);
            }
        }
    };

    //服务绑定结果回调
    public interface OnServiceBindListener{
        void onBind(BluetoothLeService bluetoothLeService,boolean isSuccess);
    }
}
