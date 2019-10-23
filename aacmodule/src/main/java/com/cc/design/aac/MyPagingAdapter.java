package com.cc.design.aac;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @类描述：
 * @作者： zhenglecheng
 * @创建时间： 2019/10/23 18:42
 */
public class MyPagingAdapter extends PagedListAdapter<List<String>,MyPagingAdapter.ViewHolder> {

    private Context mContext;
    private final LayoutInflater mLayoutInflater;

    protected MyPagingAdapter(Context context, @NonNull DiffUtil.ItemCallback<List<String>> diffCallback) {
        super(diffCallback);
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
