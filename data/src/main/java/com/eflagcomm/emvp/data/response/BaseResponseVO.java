package com.eflagcomm.emvp.data.response;

public class BaseResponseVO<T> {
    public int errorCode;
    public String errorMsg;
    public T data;
}
