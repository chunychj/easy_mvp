package com.eflagcomm.emvp;

import android.content.Intent;

import com.eflagcomm.emvp.base.ui.AbsBaseActivity;
import com.eflagcomm.emvp.base.utils.LogUtil;
import com.eflagcomm.emvp.base.utils.ToastUtil;
import com.eflagcomm.emvp.data.response.HomeArticleRespVO;
import com.eflagcomm.emvp.presenter.TestPresenter;
import com.eflagcomm.emvp.ui.WidgetActivity;
import com.eflagcomm.emvp.view.ITestView;

import butterknife.OnClick;

/**
 * <p>主页</p >
 *
 * @author zhenglecheng
 * @date 2019/12/26
 */
public class MainActivity extends AbsBaseActivity<TestPresenter> implements ITestView {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected TestPresenter getPresenter() {
        return new TestPresenter(this, this);
    }

    @OnClick(R.id.button_test)
    void onClickButtonTest() {
        mPresenter.getHomeArticles(1);
    }

    @OnClick(R.id.button_view)
    void onClickButtonView() {
        Intent intent = new Intent(this, WidgetActivity.class);
        startActivity(intent);
    }

    @Override
    public void setData(HomeArticleRespVO.Data data) {
        LogUtil.e(TAG, data != null ? data.toString() : null);
    }

    @Override
    public void onError(String msg, String code) {
        ToastUtil.showShortToast(this, msg);
    }
}
