package com.cc.cdesign.base.adapter;

/**
 * @类描述：
 * @作者： zhenglecheng
 * @创建时间： 2019/9/29 16:59
 */
public interface IBaseAdapter {

    /**
     * 加载布局文件id
     * @return id
     */
     int getLayoutId();

    /**
     * 数据转化
     */
     void convert();

}
