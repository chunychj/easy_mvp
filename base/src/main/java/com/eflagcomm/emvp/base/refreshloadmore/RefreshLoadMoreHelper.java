package com.eflagcomm.emvp.base.refreshloadmore;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

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
    default SmartRefreshLoadMore initSmartRefreshLoadMore() {
        SmartRefreshLoadMore refreshLoadMore = null;
        try {
            refreshLoadMore = SmartRefreshLoadMore.getRefreshLoadMore(getSmartRefreshLayout());
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

    /**
     * 获取刷新控件
     */
    SmartRefreshLayout getSmartRefreshLayout();

    @Override
    default void onRefresh(RefreshLayout refreshLayout) {
        refreshData();
    }

    @Override
    default void onLoadMore(RefreshLayout refreshLayout) {
        loadMoreData();
    }


}
