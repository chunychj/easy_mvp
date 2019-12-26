package com.eflagcomm.emvp.ui.fragment;

import com.eflagcomm.emvp.widget.SmileFaceView;
import com.eflagcomm.emvp.R;

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
