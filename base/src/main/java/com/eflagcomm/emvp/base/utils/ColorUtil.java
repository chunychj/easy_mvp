package com.eflagcomm.emvp.base.utils;

import android.content.Context;

import androidx.core.content.ContextCompat;

/**
 * <p>颜色工具帮助类{d}</p>
 *
 * @author zhenglecheng
 * @date 2019-12-13
 */
public class ColorUtil {
    private ColorUtil() {

    }

    public static int getColor(Context context, int corId) {
        return ContextCompat.getColor(context, corId);
    }
}
