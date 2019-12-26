package com.rocago.nuojia.ble.bluetooth.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.eflagcomm.emvp.base.utils.LogUtil;
import com.rocago.nuojia.ble.R;
import com.rocago.nuojia.ble.bluetooth.constant.DeviceConstants;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zlc
 * email : zlc921022@163.com
 * desc : 按摩仪操作自定义button
 * 19年6月13
 */
public class ChairButton extends AppCompatButton {

    private static final String TAG = "MaChairButton";
    /**
     * 用来加锁一段时间内发送长按事件指令
     */
    private volatile boolean isStart = true;
    /**
     * 负责长按事件发送指令
     */
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    public ChairButton(Context context) {
        super(context);
    }

    public ChairButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs, context);
    }

    public ChairButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs, context);
    }

    private void initAttrs(AttributeSet attrs, Context context) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ChairButton);
        ta.recycle();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.e(TAG, "我是down事件");
                if (mButtonOnClickListener != null) {
                    mButtonOnClickListener.onTouchClick(ChairButton.this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                printMoveEvent();
                break;
            case MotionEvent.ACTION_UP:
                LogUtil.e(TAG, "我是up事件");
                if (mButtonOnClickListener != null) {
                    mButtonOnClickListener.onTouchClickUp(ChairButton.this);
                }
                break;
            default:
        }
        return super.onTouchEvent(event);
    }

    /**
     * move事件
     */
    private void onTouchLongMove(MotionEvent event) {
//        long moveTime = System.currentTimeMillis();
//        long downTime = event.getDownTime();
//        long eventTime = event.getEventTime();
//        if (!mIsLongClick.get()) {
//            LogUtil.e("时间差1：", (moveTime - mDownTime) + "");
//            LogUtil.e("时间差2：", (eventTime - downTime) + "");
//            mIsLongClick.set(isLongPressed(mDownTime, moveTime));
//        }
//        //长按模式下每隔一定时间发送一个延迟消息
//        if (isStart && mIsExeLongClick) {
//            LogUtil.e(TAG, "长按事件开始执行了，开始执行时间：" + (eventTime - downTime));
//            isStart = false;
//            mMainHandler.postDelayed(mMainRunnable, 50);
//        }
    }

    /**
     * 双重计时
     *
     * @return true 是长按事件 false 不是
     */
    public boolean isLongClickEvent(AtomicBoolean isLongClick, boolean isExeLongClick) {
        return (isLongClick != null && isLongClick.get()) || isExeLongClick;
    }

    /**
     * 执行点击抬起事件
     *
     * @param event
     */
    private void onTouchUpClick(MotionEvent event) {
        //down事件到up事件大于一定事件间隔才算做点击事件，防止误触
        if (isClickedEvent(event) && mButtonOnClickListener != null) {
            mButtonOnClickListener.onTouchClick(this);
            mMainHandler.postDelayed(() -> {
                mButtonOnClickListener.onTouchClickUp(this);
            }, 50);
        }
    }


    /**
     * 重置up事件变量
     */
    public void resetUpEvent() {
        //up事件之后变量状态还原
//        this.mMainHandler.removeCallbacks(mMainRunnable);
//        this.isStart = true;
//        this.isDownEvent = false;
//        this.isUpEvent = false;
//        this.mIsExeLongClick = false;
//        this.mIsLongClick.set(false);
//        this.mCountTimeHandler.removeCallbacks(mCountTimeRunnable);
//        this.index = 0;
    }

    /**
     * 是否是点击事件
     *
     * @return true 是 false 否
     */
    private boolean isClickedEvent(MotionEvent event) {
        long eventTime = event.getEventTime();
        long downTime = event.getDownTime();
        return Math.abs(eventTime - downTime) >= DeviceConstants.CLICK_TIME;
    }

    /**
     * 监测是否是长按事件
     */
    private synchronized boolean isLongPressed(long downTime, long moveTime) {
        //时间差
        long offsetTime = Math.abs(moveTime - downTime);
        return offsetTime >= DeviceConstants.LONG_CLICK_TIME;
    }

    /**
     * 添加点击延迟时间 发送指令
     */
    private final Runnable mMainRunnable = new Runnable() {
        @Override
        public void run() {
            if (mButtonOnClickListener != null) {
                mButtonOnClickListener.onTouchClick(ChairButton.this);
            }
        }
    };


    /**
     * 打印move事件 一次按摩事件只打印一次
     */
    private void printMoveEvent() {
//        if (isPrint) {
//            LogUtil.e(TAG, "我是move事件");
//            isPrint = false;
//        }
    }


    /**
     * 按钮点击事件
     */
    public interface OnButtonClickListener {
        /**
         * 点击事件
         */
        void onTouchClick(View v);

        /**
         * 点击抬起事件
         */
        void onTouchClickUp(View v);
    }

    private OnButtonClickListener mButtonOnClickListener;

    public void setButtonOnClickListener(OnButtonClickListener buttonOnClickListener) {
        mButtonOnClickListener = buttonOnClickListener;
    }

    /**
     * 按钮长按事件
     */
    public interface OnButtonLongClickListener {
        /**
         * 长按事件
         */
        void onTouchLongClick(View v);

        /**
         * 长按事件/点击事件抬起事件
         */
        void onTouchLongClickUp(View v);
    }

    private OnButtonLongClickListener mButtonOnLongClickListener;

    public void setButtonOnLongClickListener(OnButtonLongClickListener buttonOnLongClickListener) {
        mButtonOnLongClickListener = buttonOnLongClickListener;
    }
}
