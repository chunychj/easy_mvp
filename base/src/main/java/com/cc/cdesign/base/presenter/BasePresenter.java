package com.cc.cdesign.base.presenter;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * @author zhenglecheng
 * @类描述：
 * @作者： zhenglecheng
 * @创建时间： 2019/10/28 18:43
 */
public abstract class BasePresenter implements LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreateView(@NonNull LifecycleOwner lifecycleOwner) {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDetachView(@NonNull LifecycleOwner lifecycleOwner) {

    }
}
