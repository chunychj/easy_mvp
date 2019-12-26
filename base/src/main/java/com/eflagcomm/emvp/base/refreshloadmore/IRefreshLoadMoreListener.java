package com.eflagcomm.emvp.base.refreshloadmore;

import com.scwang.smartrefresh.layout.api.RefreshLayout;

/**
 * @author zhenglecheng
 * @类描述：
 * @作者： zhenglecheng
 * @创建时间： 2019/9/29 16:51
 */
public interface IRefreshLoadMoreListener {

    /**
     * 刷新数据
     */
    void onRefresh(RefreshLayout refreshLayout);

    /**
     * 加载更多数据
     */
    void onLoadMore(RefreshLayout refreshLayout);
}
