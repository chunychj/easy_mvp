package com.eflagcomm.emvp.ble.fastble.callback;


import com.eflagcomm.emvp.ble.fastble.data.BleDevice;

import java.util.List;

public abstract class BleScanCallback implements BleScanPresenterImp {

    public  void onScanFinished(List<BleDevice> scanResultList){

    }

    public void onLeScan(BleDevice bleDevice) {
    }
}
