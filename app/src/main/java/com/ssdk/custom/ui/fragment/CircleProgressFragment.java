package com.ssdk.custom.ui.fragment;

import android.view.View;

import com.ssdk.custom.R;
import com.ssdk.custom.ui.view.CircleRingProgress;

/**
 * Created by zlc on 2018/7/16.
 */

public class CircleProgressFragment extends BaseFragment{
    private CircleRingProgress mRingProgress;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_circle_progress;
    }

    @Override
    protected void initView() {
        super.initView();
        mRingProgress = findView(R.id.ring_progress);
    }

    @Override
    protected void initData() {
    }

    public void progressAnimation(){
        mRingProgress.setProgressAnimation(0,60,1000);
    }
}
