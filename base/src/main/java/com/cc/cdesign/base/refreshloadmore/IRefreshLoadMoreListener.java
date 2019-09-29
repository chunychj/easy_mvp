package com.cc.cdesign.base.refreshloadmore;

/**
 * @类描述：
 * @作者： zhenglecheng
 * @创建时间： 2019/9/29 16:51
 */
public interface IRefreshLoadMoreListener {

    /**
     * 刷新数据
     */
    void onRefresh();

    /**
     * 加载更多数据
     */
    void onLoadMore();
}
