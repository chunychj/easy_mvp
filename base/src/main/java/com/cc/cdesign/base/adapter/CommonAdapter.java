package com.cc.cdesign.base.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zlc on 2016/6/22.
 * ListView和GridView的万能适配器
 */
@SuppressWarnings("all")
public abstract class CommonAdapter<T> extends BaseAdapter {

    protected List<T> mDatas;
    private LayoutInflater mLayoutInflater;
    protected int mLayoutResId;

    public CommonAdapter(@NonNull int layoutResId) {
        this.mDatas = new ArrayList<>();
        this.mLayoutResId = layoutResId;
    }

    public CommonAdapter(@NonNull List<T> datas, @NonNull int layoutResId) {
        this.mDatas = datas;
        this.mLayoutResId = layoutResId;
    }

    @Override
    public int getCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    @Override
    public T getItem(int position) {
        if (mDatas != null || position < mDatas.size()) {
            return mDatas.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setNewData(List<T> datas) {
        if (mDatas != null && mDatas.size() > 0) {
            mDatas.clear();
        }
        mDatas = datas;
        notifyDataSetChanged();
    }

    public void addData(List<T> datas) {
        if (datas == null || datas.isEmpty()) {
            return;
        }
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }
        mDatas.addAll(datas);
    }

    public void removeAll() {
        if (mDatas != null) {
            mDatas.clear();
        }
    }

    public T remove(int position) {
        T delValue = null;
        if (mDatas != null && position < mDatas.size()) {
            delValue = mDatas.get(position);
            mDatas.remove(position);
        }
        return delValue;
    }

    public T update(int position, T t) {
        T updValue = null;
        if (mDatas != null && position < mDatas.size()) {
            updValue = mDatas.get(position);
            mDatas.set(position, t);
        }
        return updValue;
    }

    public List<T> getDatas() {
        return mDatas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder holder = CommonViewHolder.getHolder(parent.getContext(), convertView,
                parent, mLayoutResId, position);
        this.convert(holder, position);
        return holder.getConvertView();
    }

    /**
     * 数据处理
     *
     * @param holder   holder对象
     * @param position 条目位置
     */
    protected abstract void convert(CommonViewHolder holder, int position);
}
