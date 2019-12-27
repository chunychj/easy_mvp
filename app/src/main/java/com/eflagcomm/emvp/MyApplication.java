package com.eflagcomm.emvp;

import android.app.Application;

import com.eflagcomm.emvp.ApiConstants;
import com.eflagcomm.emvp.base.BaseApplication;
import com.eflagcomm.emvp.base.utils.AppLogger;
import com.eflagcomm.emvp.data.HttpProvider;

/**
 * <p></p >
 *
 * @author zhenglecheng
 * @date 2019/12/26
 */
public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        AppLogger.init(true);
        boolean isDebug = true;
        HttpProvider.createProvider(getBaseUrl(), isDebug,this);
    }

    private String getBaseUrl(){
        return ApiConstants.BASE_URL;
    }
}
