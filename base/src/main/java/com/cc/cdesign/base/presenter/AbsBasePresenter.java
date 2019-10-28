package com.cc.cdesign.base.presenter;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.cc.cdesign.base.view.BaseView;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.AutoDisposeConverter;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

/**
 * @author zlc
 * email : zlc921022@163.com
 * desc :
 */
public abstract class AbsBasePresenter<V extends BaseView> extends BasePresenter {

    private LifecycleOwner mLifecycleOwner;

    public AbsBasePresenter() {
    }

    @Override
    public void onCreateView(@NonNull LifecycleOwner lifecycleOwner) {
        super.onCreateView(lifecycleOwner);
        this.mLifecycleOwner = lifecycleOwner;
    }

    @Override
    public void onDetachView(@NonNull LifecycleOwner lifecycleOwner) {
        super.onDetachView(lifecycleOwner);
    }

    public <T> AutoDisposeConverter<T> bindLifeCycle() {
        if (mLifecycleOwner == null) {
            throw new NullPointerException("lifecycleOwner 对象为空");
        }
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(mLifecycleOwner));
    }

}
