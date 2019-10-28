package com.cc.cdesign.base.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.cc.cdesign.base.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;


/**
 * @author zhenglecheng
 * @类描述：recyclerView适配器基类
 * @作者： zhenglecheng
 * @创建时间： 2019/8/2 10:46
 */
@SuppressWarnings("all")
public abstract class BaseRecyclerViewAdapter<T, K extends BaseViewHolder> extends BaseQuickAdapter<T, K> {

    /**
     * 是否动态设置过空布局宽高
     */
    private boolean isSetEmptyParams = false;
    /**
     * 当列表没有数据时候，是否同时显示空布局和头布局，app中默认设置为显示
     */
    private boolean isShowHeader = true;

    public BaseRecyclerViewAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    public BaseRecyclerViewAdapter(@Nullable List<T> data) {
        super(data);
    }

    public BaseRecyclerViewAdapter(int layoutResId) {
        super(layoutResId);
    }

    /**
     * 数据处理 加载方法
     *
     * @param helper BaseViewHolder
     * @param item   数据对象
     */
    @Override
    protected abstract void convert(K helper, T item);

    @Override
    public void setNewData(@Nullable List<T> data) {
        super.setNewData(data);
        setEmptyViewVisibility();
    }

    @Override
    public void bindToRecyclerView(RecyclerView recyclerView) {
        super.bindToRecyclerView(recyclerView);
        //存在头布局，则直接显示
        if (getHeaderLayoutCount() == 1) {
            setHeaderAndEmpty(true);
        }
        //空布局需要在bindToRecyclerView方法之后调用 否则会抛出异常
        setEmptyView(getEmptyViewId(), recyclerView);
        getEmptyView().setVisibility(View.GONE);
        //减少暂无数据点击区域，不让点击整个空布局
        getEmptyView().setOnClickListener(v -> {
            if (mOnEmptyClickListener != null) {
                mOnEmptyClickListener.onEmptyClick(v);
            }
        });
    }

    /**
     * 设置空View显示状态
     */
    public void setEmptyViewVisibility() {
        setEmptyViewVisibility("暂无数据", false);
    }

    /**
     * 设置空View显示状态
     *
     * @param isLoadMore true 加载更多 false 刷新或者进来加载数据
     */
    public void setEmptyViewVisibility(boolean isLoadMore) {
        setEmptyViewVisibility("暂无数据", isLoadMore);
    }

    /**
     * 设置空View显示状态
     *
     * @param text 设置错误提示文字
     */
    public void setEmptyViewVisibility(String text, boolean isLoadMore) {
        if (getEmptyView() == null || isLoadMore) {
            return;
        }
        boolean isEmpty = getEmptyViewCount() == 1;
        getEmptyView().setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        if (getHeaderLayoutCount() == 1) {
            setHeaderAndEmpty(isEmpty && isShowHeader);
            setEmptyViewParams(isEmpty);
        }
        //给文本设置错误提示信息
        TextView errorMessage = getEmptyView().findViewById(R.id.tv_error_message);
        errorMessage.setText(text);
    }

    /**
     * 存在头布局的时候，需要重新计算一下空布局高度，不然会存在空布局也能滑动问题
     *
     * @param isEmptyLayout true 显示空布局 false 不显示
     */
    private void setEmptyViewParams(boolean isEmptyLayout) {
        if (getRecyclerView() == null || !isEmptyLayout || isSetEmptyParams) {
            return;
        }
        try {
            isSetEmptyParams = true;
            final View emptyView = getEmptyView();
            emptyView.postDelayed(() -> {
                mContext = emptyView.getContext();
                int recyclerViewHeight = getRecyclerView().getMeasuredHeight();
                //头布局高度
                int headHeight = getHeaderLayout() != null ? getHeaderLayout().getMeasuredHeight() : 0;
                //尾布局高度
                int footHeight = getFooterLayout() != null ? getFooterLayout().getMeasuredHeight() : 0;
                //重新计算空布局高度
                ViewGroup.LayoutParams params = emptyView.getLayoutParams();
                params.height = recyclerViewHeight - headHeight - footHeight;
                emptyView.setLayoutParams(params);
            }, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 当数据集合为空的时候是否显示头布局
     *
     * @param isHeadAndEmpty true 显示头布局和空布局 false 只显示空布局   默认false
     */
    @Override
    public void setHeaderAndEmpty(boolean isHeadAndEmpty) {
        super.setHeaderAndEmpty(isHeadAndEmpty);
    }

    /**
     * 当列表数据集为空时，如果不想显示头布局，可以调用这个方法，app中已默认同时显示空布局和头布局了
     *
     * @param showHeader true 显示 false 不显示 默认true
     */
    public void setShowHeader(boolean showHeader) {
        isShowHeader = showHeader;
    }

    /**
     * 空布局layout
     *
     * @return
     */
    protected int getEmptyViewId() {
        return R.layout.layout_recyclerview_empty_data;
    }

    /**
     * 空布局回调点击事件
     */
    public interface OnEmptyClickListener {
        /**
         * 空布局点击
         *
         * @param v 空view
         */
        void onEmptyClick(View v);
    }

    private OnEmptyClickListener mOnEmptyClickListener;

    public void setOnEmptyClickListener(OnEmptyClickListener onEmptyClickListener) {
        mOnEmptyClickListener = onEmptyClickListener;
    }

}
