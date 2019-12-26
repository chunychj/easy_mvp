package com.eflagcomm.emvp.ble.bluetooth.constant;

/**
 * @author zlc
 * email : zlc921022@163.com
 * desc : 切换蓝牙操作方式类
 */
public final class DeviceConstants {
    //用服务形式操作蓝牙
    public static final int SERVICE = 0;
    //fastble库操作蓝牙
    public static final int FASTBLE = 1;
    //点击事件执行时间
    public static final long CLICK_TIME = 60;
    //判断长按事件时间
    public static final long LONG_CLICK_TIME = 1000;
    //延迟时间（发送指令）
    public static final long DELAY_TIME = 200;
}
