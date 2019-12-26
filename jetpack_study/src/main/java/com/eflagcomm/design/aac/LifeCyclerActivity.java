package com.eflagcomm.design.aac;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProviders;

/**
 * @类描述：
 * @作者： zhenglecheng
 * @创建时间： 2019/10/12 17:56
 */
public class LifeCyclerActivity extends AppCompatActivity {

    private TextView mTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifecycle);
        getLifecycle().addObserver(new MyObserver());

        mTv = findViewById(R.id.tv);

        liveDataAndViewModel();
    }

    private void liveDataAndViewModel() {
        final MyViewModel model = ViewModelProviders.of(this).get(MyViewModel.class);
        model.getLiveData().observe(this,myObserver);
        LiveData<Integer> data = Transformations.map(model.getLiveData(), new Function<String, Integer>() {
            @Override
            public Integer apply(String input) {
                return input.length();
            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.getLiveData().setValue("数据改变啦");
            }
        });
    }

    Observer myObserver = new Observer<String>() {
        @Override
        public void onChanged(String str) {
            mTv.setText(str);
        }
    };

}
