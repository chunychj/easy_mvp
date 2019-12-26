package com.rocago.nuojia.ble.fastble.callback;


import com.rocago.nuojia.ble.fastble.data.BleDevice;

import java.util.List;

public abstract class BleScanCallback implements BleScanPresenterImp {

    public  void onScanFinished(List<BleDevice> scanResultList){

    }

    public void onLeScan(BleDevice bleDevice) {
    }
}
