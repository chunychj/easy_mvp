package com.cc.cdesign.base.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by zlc on 2016/6/22.
 * 通用的ViewHolder
 */
public class CommonViewHolder {

    private final SparseArray<View> mViews;
    private View mConvertView;
    private CommonViewHolder(Context context, ViewGroup parent, int layoutId, int position){

        this.mViews = new SparseArray<>();
        this.mConvertView = LayoutInflater.from(context).inflate(
                layoutId,parent,false);
        mConvertView.setTag(this);
    }

    //获取ViewHolder对象
    public static CommonViewHolder getHolder(Context context, View convertView, ViewGroup parent,
                                             int layoutId, int position){
        if(convertView == null){
            return new CommonViewHolder(context,parent,layoutId,position);
        }else{
            return (CommonViewHolder) convertView.getTag();
        }
    }


    public <T extends View> T getView(int viewId){
        View view = mViews.get(viewId);
        if(view == null){
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return (T) view;
    }




    public CommonViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        if(tv != null) {
            tv.setText(text);
        }
        return this;
    }



    public CommonViewHolder setTextColor(int viewId, String color) {
        TextView tv = getView(viewId);
        if(tv != null) {
            tv.setTextColor(Color.parseColor(color));
        }
        return this;
    }



    public CommonViewHolder setImageResource(int viewId, int resId){
        ImageView imageView = getView(viewId);
        if(imageView != null) {
            imageView.setImageResource(resId);
        }
        return this;
    }


    public CommonViewHolder setImageBitmap(int viewId, Bitmap bitmap){
        ImageView imageView = getView(viewId);
        if(imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
        return this;
    }

    public View getConvertView() {
        return mConvertView;
    }
}
