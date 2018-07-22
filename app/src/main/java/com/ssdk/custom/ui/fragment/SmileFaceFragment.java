package com.ssdk.custom.ui.fragment;

import android.view.View;

import com.ssdk.custom.R;
import com.ssdk.custom.ui.view.SmileFaceView;

/**
 * Created by zlc on 2018/7/16.
 */

public class SmileFaceFragment extends BaseFragment {

    private SmileFaceView mSmileView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_smile_face;
    }

    @Override
    protected void initView() {
        super.initView();
        mSmileView = findView(R.id.smile_view);
    }

    @Override
    protected void initData() {
    }

    public void smileAnimation(){
        mSmileView.playAnimation();
    }
}
