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
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;

/**
 * Created by zlc on 2018/11/22
 * Email: zheng.lecheng@eflagcomm.com
 * Desc: 自定义经典上拉加载尾部
 */
public class ClassicsFooter extends LinearLayout implements RefreshFooter {

    private LayoutInflater mLayoutInflater;
    //true 表示没有更多数据  false表示有更多数据
    protected boolean mNoMoreData = false;
    public static final String REFRESH_FOOTER_PULLING = "上拉加载更多";
    public static final String REFRESH_FOOTER_RELEASE = "释放立即加载";
    public static final String REFRESH_FOOTER_LOADING =  "正在加载";
    public static final String REFRESH_FOOTER_FINISH = "加载完成";
    public static final String REFRESH_FOOTER_FAILED = "加载失败";
    public static final String REFRESH_FOOTER_NOTHING = "没有更多数据了";
    private static final int FOOTER_HEIGHT = 100;
    private ImageView mIvProgressView;
    private TextView mTvLoadMoreText;
    private ProgressDrawable mProgressDrawable;

    public ClassicsFooter(Context context) {
        this(context,null);
    }

    public ClassicsFooter(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ClassicsFooter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLayoutInflater = LayoutInflater.from(context);
        initView();
    }

    private void initView() {
        setGravity(Gravity.CENTER);
        View footView = mLayoutInflater.inflate(R.layout.loadmore_footer_layout,
                this, true);
        mTvLoadMoreText = footView.findViewById(R.id.tv_loadmore_text);
        mProgressDrawable = new ProgressDrawable();
        mProgressDrawable.setColor(getResources().getColor(R.color.textColorPrimary));
        mIvProgressView = footView.findViewById(R.id.iv_footer_progress);
        mIvProgressView.setImageDrawable(mProgressDrawable);
        setMinimumHeight(FOOTER_HEIGHT);
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        if (mNoMoreData != noMoreData) {
            mNoMoreData = noMoreData;
            final View arrowView = mIvProgressView;
            if (noMoreData) {
                mTvLoadMoreText.setText(REFRESH_FOOTER_NOTHING);
                arrowView.setVisibility(GONE);
            } else {
                mTvLoadMoreText.setText(REFRESH_FOOTER_PULLING);
                arrowView.setVisibility(VISIBLE);
            }
        }
        return true;
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        if( !mNoMoreData){
            mProgressDrawable.start();
        }
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        if( !mNoMoreData){
            mProgressDrawable.stop();
            mTvLoadMoreText.setText(success ? REFRESH_FOOTER_FINISH : REFRESH_FOOTER_FAILED);
        }
        return 0;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        if( !mNoMoreData){
            switch (newState){
                //上拉加载
                case None:
                case PullUpToLoad:
                    mTvLoadMoreText.setText(REFRESH_FOOTER_PULLING);
                    mIvProgressView.setVisibility(GONE);
                    break;
                //加载中
                case Loading:
                case LoadReleased:
                    mTvLoadMoreText.setText(REFRESH_FOOTER_LOADING);
                    mIvProgressView.setVisibility(VISIBLE);
                    break;
                //释放去加载
                case ReleaseToLoad:
                    mTvLoadMoreText.setText(REFRESH_FOOTER_RELEASE);
                    mIvProgressView.setVisibility(VISIBLE);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setPrimaryColors(int... colors) {
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {}

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {}

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {}


    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {}

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }
}
