package com.rocago.nuojia.ble.bluetooth;

/**
 * @author zlc
 * email : zlc921022@163.com
 * desc : 设备写入字节数组接口
 */
public interface IBleDeviceWriteBytes {

    /**
     * 获取需要写入的字节数据
     *
     * @return
     */
    default byte[] getWriteBytes(String... cmds) {
        return null;
    }

    /**
     * 按摩仪类实现
     *
     * @param cmd
     * @return
     */
    default byte[] getUpBytes(String cmd) {
        return null;
    }

    /**
     * 按摩仪类实现
     *
     * @param cmd
     * @return
     */
    default byte[] getDownBytes(String cmd) {
        return null;
    }
}
