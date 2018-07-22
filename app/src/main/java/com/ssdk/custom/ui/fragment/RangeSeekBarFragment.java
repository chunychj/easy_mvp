package com.ssdk.custom.ui.fragment;

import android.view.MotionEvent;
import android.view.View;

import com.ssdk.custom.R;
import com.ssdk.custom.ui.view.viewgroup.RangeSeekBar;

/**
 * Created by zlc on 2018/7/16.
 */

public class RangeSeekBarFragment extends BaseFragment{

    private RangeSeekBar mSeekbar;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_range_seekbar;
    }

    @Override
    protected void initView() {
        super.initView();
        mSeekbar = findView(R.id.seekbar);
    }

    @Override
    protected void initData() {
    }

    @Override
    public void initListener() {
        super.initListener();
    }
}
