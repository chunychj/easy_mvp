package com.eflagcomm.emvp.base.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.eflagcomm.emvp.base.R;
import com.tbruyelle.rxpermissions2.RxPermissions;

/**
 * @author zlc
 * email : zlc921022@163.com
 * desc : 权限检查帮助类
 */
public class PermissionCheckHelper {

    private Context mContext;
    private MaterialDialog mShowDialog;

    private PermissionCheckHelper(Context context) {
        this.mContext = context;
    }

    public static PermissionCheckHelper getPermissionHelper(Context context) {
        return new PermissionCheckHelper(context);
    }

    @SuppressLint("CheckResult")
    public void scanCheckPermission(OnPermissionCheckListener listener) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED)) {
                if (listener != null) {
                    listener.toScanActivity();
                }
            } else {
                // 启动权限
                final RxPermissions rxPermissions = new RxPermissions((FragmentActivity) mContext);
                rxPermissions.request(Manifest.permission.CAMERA)
                        .subscribe(granted -> {
                            if (granted) {
                                if (listener != null) {
                                    listener.toScanActivity();
                                }
                            } else {
                                showDialog();
                            }
                        }, throwable -> LogUtil.e("scanCheckPermission " + " 权限请求error", throwable.getMessage()));
            }
        } else {
            if (listener != null) {
                listener.toScanActivity();
            }
        }
    }

    /**
     * 设置请求权限的对话框
     */
    private void showDialog() {
        if (mShowDialog == null) {
            mShowDialog = new MaterialDialog.Builder(mContext)
                    .content(R.string.b_permission)
                    .widgetColorRes(R.color.colorPrimary)
                    .positiveText("设置")
                    .contentColorRes(R.color.textColorPrimary)
                    .negativeText("取消")
                    .onPositive((dialog, which) -> {
                        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS",
                                Uri.fromParts("package", mContext.getPackageName(), null));
                        mContext.startActivity(intent);
                        dialog.dismiss();
                    }).onNegative((dialog, which) -> {
                        dialog.dismiss();
                    }).cancelable(false)
                    .build();
        }
        if (!mShowDialog.isShowing()) {
            mShowDialog.show();
        }
    }

    public interface OnPermissionCheckListener {
        void toScanActivity();
    }

}
