package com.eflagcomm.emvp.ble.fastble.exception;


public class TimeoutException extends BleException {

    public TimeoutException() {
        super(ERROR_CODE_TIMEOUT, "Timeout Exception Occurred!");
    }

}
