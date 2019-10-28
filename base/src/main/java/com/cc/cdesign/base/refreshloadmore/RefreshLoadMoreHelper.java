package com.cc.cdesign.base.refreshloadmore;

import androidx.annotation.NonNull;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * @类描述：刷新和加载更多帮助类
 * @作者： zhenglecheng
 * @创建时间： 2019/10/28 18:22
 */
@SuppressWarnings("all")
public interface RefreshLoadMoreHelper extends IRefreshLoadMoreListener {

    /**
     * 初始化对象SmartRefreshLoadMore
     */
    default SmartRefreshLoadMore initSmartRefreshLoadMore(@NonNull SmartRefreshLayout smartRefreshLayout) {
        SmartRefreshLoadMore refreshLoadMore = null;
        try {
            refreshLoadMore = SmartRefreshLoadMore.getRefreshLoadMore(smartRefreshLayout);
            refreshLoadMore.setEnableLoadMore(false);
            refreshLoadMore.setOnRefreshLoadMoreListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return refreshLoadMore;
    }

    /**
     * 刷新数据
     */
    void refreshData();

    /**
     * 加载更多数据
     */
    void loadMoreData();


    @Override
    default void onRefresh() {
        refreshData();
    }

    @Override
    default void onLoadMore() {
        loadMoreData();
    }
}
