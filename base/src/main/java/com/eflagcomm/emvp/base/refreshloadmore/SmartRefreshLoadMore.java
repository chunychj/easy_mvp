package com.eflagcomm.emvp.base.refreshloadmore;

import androidx.annotation.NonNull;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

/**
 * Email: zheng.lecheng@eflagcomm.com
 * Desc: 下拉刷新上拉加载类
 * 控件地址：https://github.com.scwang90/SmartRefreshLayout
 *
 * @author zhenglecheng
 */
public class SmartRefreshLoadMore {

    static {
        initDefaultHeaderFooter();
    }

    private SmartRefreshLayout mRefreshLayout;

    private SmartRefreshLoadMore(SmartRefreshLayout refreshLayout) {
        if (refreshLayout == null) {
            throw new NullPointerException("refreshLayout is null!");
        }
        this.mRefreshLayout = refreshLayout;
    }

    public static SmartRefreshLoadMore getRefreshLoadMore(SmartRefreshLayout refreshLayout) {
        return new SmartRefreshLoadMore(refreshLayout);
    }

    /**
     * 设置能否自动刷新
     *
     * @param autoRefresh
     */
    private SmartRefreshLoadMore setAutoRefresh(boolean autoRefresh) {
        if (autoRefresh) {
            mRefreshLayout.autoRefresh();
        }
        return this;
    }

    /**
     * 设置能否自动刷新
     *
     * @param autoRefresh
     * @param delayed     延迟delayed毫秒后自动刷新
     */
    private SmartRefreshLoadMore setAutoRefresh(boolean autoRefresh, int delayed) {
        if (autoRefresh) {
            mRefreshLayout.autoRefresh(delayed);
        }
        return this;
    }

    /**
     * 设置能否自动加载更多
     *
     * @param autoLoadMore
     */
    private SmartRefreshLoadMore setAutoLoadMore(boolean autoLoadMore) {
        if (autoLoadMore) {
            mRefreshLayout.autoLoadMore();
        }
        return this;
    }

    /**
     * 设置能否自动加载更多
     *
     * @param autoLoadMore
     * @param delayed      延迟delayed毫秒后自动加载更多
     */
    private SmartRefreshLoadMore setAutoLoadMore(boolean autoLoadMore, int delayed) {
        if (autoLoadMore) {
            mRefreshLayout.autoLoadMore();
        }
        return this;
    }

    /**
     * 结束刷新
     *
     * @param success true 成功 false 失败
     */
    public SmartRefreshLoadMore finishRefresh(boolean success) {
        mRefreshLayout.finishRefresh(success);
        return this;
    }

    /**
     * 结束刷新
     *
     * @param success true 成功 false 失败
     * @param delayed 延迟delayed毫秒后结束刷新
     */
    public SmartRefreshLoadMore finishRefresh(boolean success, int delayed) {
        mRefreshLayout.finishRefresh(delayed);
        return this;
    }

    /**
     * 结束加载更多
     *
     * @param success true 成功 false 失败
     */
    public SmartRefreshLoadMore finishLoadMore(boolean success) {
        mRefreshLayout.finishLoadMore(success);
        return this;
    }

    /**
     * 结束加载更多
     *
     * @param success    true 成功 false 失败
     * @param delayed    延迟时间
     * @param noMoreData 是否还有更多数据
     */
    public SmartRefreshLoadMore finishLoadMore(boolean success, int delayed, boolean noMoreData) {
        mRefreshLayout.finishLoadMore(delayed, success, noMoreData);
        return this;
    }

    /**
     * 显示加载完成，并不再触发加载更多事件
     *
     * @return
     */
    public SmartRefreshLoadMore finishLoadMoreWithNoMoreData() {
        mRefreshLayout.finishLoadMoreWithNoMoreData();
        return this;
    }

    /**
     * 设置是否还有更多数据
     *
     * @param noMoreData true 没有更多  false 还有更多
     * @return
     */
    public SmartRefreshLoadMore setNoMoreData(boolean noMoreData) {
        mRefreshLayout.setNoMoreData(noMoreData);
        return this;
    }

    /**
     * 是否开启下拉刷新  不设置默认为true
     *
     * @param refresh
     * @return
     */
    public SmartRefreshLoadMore setEnableRefresh(boolean refresh) {
        mRefreshLayout.setEnableRefresh(refresh);
        return this;
    }

    /**
     * 是否开启加载更多 不设置默认false-智能开启
     *
     * @param loadMore
     * @return
     */
    public SmartRefreshLoadMore setEnableLoadMore(boolean loadMore) {
        mRefreshLayout.setEnableLoadMore(loadMore);
        return this;
    }

    /**
     * 是否监听到列表滚动到底部时触发加载事件  默认true
     *
     * @param enable
     * @return
     */
    public SmartRefreshLoadMore setEnableAutoLoadMore(boolean enable) {
        mRefreshLayout.setEnableAutoLoadMore(enable);
        return this;
    }

    /**
     * 内容不满一页的收货，是否可以上拉加载更多 不设置默认false
     *
     * @param enable
     * @return
     */
    public SmartRefreshLoadMore setEnableLoadMoreWhenContentNotFull(boolean enable) {
        mRefreshLayout.setEnableLoadMoreWhenContentNotFull(enable);
        return this;
    }

    /**
     * 是否开启越界拖动 仿苹果回弹效果 不设置默认true
     *
     * @param enable
     * @return
     */
    public SmartRefreshLoadMore setEnableOverScrollDrag(boolean enable) {
        mRefreshLayout.setEnableOverScrollDrag(enable);
        return this;
    }

    /**
     * 设置刷新回弹动画时长
     *
     * @param duration 时长
     * @return
     */
    public SmartRefreshLoadMore setReboundDuration(int duration) {
        mRefreshLayout.setReboundDuration(duration);
        return this;
    }

    /**
     * 是不是刷新状态
     *
     * @return
     */
    public boolean isRefreshing() {
        return mRefreshLayout.getState() == RefreshState.Refreshing;
    }

    /**
     * 是不是加载中状态
     *
     * @return
     */
    public boolean isLoading() {
        return mRefreshLayout.getState() == RefreshState.Loading;
    }

    /**
     * 指定默认刷新头部和尾部
     */
    private static void initDefaultHeaderFooter() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> new ClassicsHeader(context));

        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> new ClassicsFooter(context));
    }

    /**
     * 设定自定义刷新头部
     *
     * @param header
     */
    public void setRefreshHeader(RefreshHeader header) {
        mRefreshLayout.setRefreshHeader(header);
    }

    /**
     * 设定自定义加载更多尾部
     *
     * @param footer
     */
    public void setRefreshFooter(RefreshFooter footer) {
        mRefreshLayout.setRefreshFooter(footer);
    }


    /**
     * 同时支持下拉刷新和加载更多的监听事件
     *
     * @param listener
     */
    public void setOnRefreshLoadMoreListener(final IRefreshLoadMoreListener listener) {
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (listener != null) {
                    listener.onLoadMore(refreshLayout);
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (listener != null) {
                    listener.onRefresh(refreshLayout);
                }
            }
        });
    }
}
