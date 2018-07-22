package com.ssdk.custom.ui.fragment;

import com.ssdk.custom.R;
import com.ssdk.custom.ui.view.PieChartView;

/**
 * Created by zlc on 2018/7/16.
 */

public class PieChartFragment extends BaseFragment{

    private PieChartView mChartView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_piechart;
    }

    @Override
    protected void initView() {
        super.initView();
        mChartView = findView(R.id.pieChart);
    }

    @Override
    protected void initData() {
    }

    public void chartAnimation(){
        mChartView.setProgressAnimation(PieChartView.DURATION);
    }
}
