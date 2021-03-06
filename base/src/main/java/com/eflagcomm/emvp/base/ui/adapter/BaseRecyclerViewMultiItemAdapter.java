package com.eflagcomm.emvp.base.ui.adapter;

import android.util.SparseIntArray;
import android.view.ViewGroup;

import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.IExpandable;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

/**
 * @author  zhenglecheng
 * @类描述：recyclerView适配器基类 针对不同布局类型
 * @作者： zhenglecheng
 * @创建时间： 2019/8/28 13:44
 */
@SuppressWarnings("all")
public abstract class BaseRecyclerViewMultiItemAdapter<T extends MultiItemEntity, K extends BaseViewHolder>
        extends BaseRecyclerViewAdapter<T, K> {

    /**
     * layouts indexed with their types
     */
    private SparseIntArray layouts;

    private static final int DEFAULT_VIEW_TYPE = -0xff;
    private static final int TYPE_NOT_FOUND = -404;

    public BaseRecyclerViewMultiItemAdapter(@Nullable List<T> data) {
        super(data);
    }

    @Override
    protected int getDefItemViewType(int position) {
        T t = mData.get(position);
        if (t != null) {
            return t.getItemType();
        }
        return DEFAULT_VIEW_TYPE;
    }

    protected void setDefaultViewTypeLayout(@LayoutRes int layoutResId) {
        addItemType(DEFAULT_VIEW_TYPE, layoutResId);
    }

    @Override
    protected K onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return createBaseViewHolder(parent, getLayoutId(viewType));
    }

    private int getLayoutId(int viewType) {
        return layouts.get(viewType, TYPE_NOT_FOUND);
    }

    protected void addItemType(int type, @LayoutRes int layoutResId) {
        if (layouts == null) {
            layouts = new SparseIntArray();
        }
        layouts.put(type, layoutResId);
    }


    @Override
    public void remove(@IntRange(from = 0L) int position) {
        if (mData == null || position < 0 || position >= mData.size()) {
            return;
        }
        T entity = mData.get(position);
        if (entity instanceof IExpandable) {
            removeAllChild((IExpandable) entity, position);
        }
        removeDataFromParent(entity);
        super.remove(position);
    }

    /**
     * 移除父控件时，若父控件处于展开状态，则先移除其所有的子控件
     *
     * @param parent         父控件实体
     * @param parentPosition 父控件位置
     */
    protected void removeAllChild(IExpandable parent, int parentPosition) {
        if (parent.isExpanded()) {
            List<MultiItemEntity> chidChilds = parent.getSubItems();
            if (chidChilds == null || chidChilds.size() == 0) {
                return;
            }
            int childSize = chidChilds.size();
            for (int i = 0; i < childSize; i++) {
                remove(parentPosition + 1);
            }
        }
    }

    /**
     * 移除子控件时，移除父控件实体类中相关子控件数据，避免关闭后再次展开数据重现
     *
     * @param child 子控件实体
     */
    protected void removeDataFromParent(T child) {
        int position = getParentPosition(child);
        if (position >= 0) {
            IExpandable parent = (IExpandable) mData.get(position);
            parent.getSubItems().remove(child);
        }
    }
}
