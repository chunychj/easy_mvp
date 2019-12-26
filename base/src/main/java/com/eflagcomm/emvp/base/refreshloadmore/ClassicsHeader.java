package com.eflagcomm.emvp.base.refreshloadmore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eflagcomm.emvp.base.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;

/**
 * Created by zlc on 2018/11/21
 * Email: zheng.lecheng@eflagcomm.com
 * Desc: 自定义经典下拉刷新头部
 */
public class ClassicsHeader extends LinearLayout implements RefreshHeader {

    private LayoutInflater mLayoutInflater;
    //标题文本
    private TextView mTvRefreshText;
    //下拉箭头
    private ImageView mIvArrowView;
    //刷新动画视图
    private ImageView mIvProgressView;
    //刷新动画
    private ProgressDrawable mProgressDrawable;

    public static final String REFRESH_HEAD_PULLING = "下拉开始刷新";
    public static final String REFRESH_HEAD_RELEASE = "释放立即刷新";
    public static final String REFRESH_HEAD_LOADING = "正在刷新";
    public static final String REFRESH_HEAD_COMPLETED = "刷新完成";
    public static final String REFRESH_HEAD_FAILURE = "刷新失败";

    private static final int HEADER_HEIGHT = 100;

    public ClassicsHeader(Context context) {
        this(context, null);
    }

    public ClassicsHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClassicsHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLayoutInflater = LayoutInflater.from(context);
        initView();
    }

    //初始化控件
    public void initView() {
        setGravity(Gravity.CENTER);
        View headView = mLayoutInflater.inflate(R.layout.refresh_head_layout,
                this, true);
        mIvArrowView = headView.findViewById(R.id.iv_arrow);
        mIvProgressView = headView.findViewById(R.id.iv_progress);
        mTvRefreshText = headView.findViewById(R.id.tv_refresh_text);
        mProgressDrawable = new ProgressDrawable();
        mProgressDrawable.setColor(getResources().getColor(R.color.textColorPrimary));
        mIvProgressView.setImageDrawable(mProgressDrawable);
        setMinimumHeight(HEADER_HEIGHT);
    }

    //获取真实视图 不能为null
    @NonNull
    @Override
    public View getView() {
        return this;
    }

    // 获取变换方式（必须指定一个：平移、拉伸、固定、全屏）
    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    //开始动画
    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        mProgressDrawable.start();
    }

    //动画结束
    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        mProgressDrawable.stop();
        mTvRefreshText.setText(success ? REFRESH_HEAD_COMPLETED : REFRESH_HEAD_FAILURE);
        return 0;
    }

    //刷新状态改变
    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        switch (newState) {
            //开始刷新
            case None:
            case PullDownToRefresh:
                mTvRefreshText.setText(REFRESH_HEAD_PULLING);
                mIvArrowView.setVisibility(VISIBLE);
                mIvProgressView.setVisibility(GONE);
                mIvArrowView.animate().rotation(0);
                break;
            //刷新中
            case Refreshing:
                mTvRefreshText.setText(REFRESH_HEAD_LOADING);
                mIvProgressView.setVisibility(VISIBLE);
                mIvArrowView.setVisibility(GONE);
                break;
            //释放刷新
            case ReleaseToRefresh:
                mTvRefreshText.setText(REFRESH_HEAD_RELEASE);
                mIvArrowView.animate().rotation(180);
                break;
            default:
        }
    }

    @Override
    public void setPrimaryColors(int... colors) {
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    //是否支持横向拖拽
    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }
}