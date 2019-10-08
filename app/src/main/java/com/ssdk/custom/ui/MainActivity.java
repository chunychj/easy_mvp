package com.ssdk.custom.ui;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.RadioGroup;

import com.ssdk.custom.R;
import com.ssdk.custom.ui.fragment.ViewFragment;
import com.ssdk.custom.ui.fragment.ViewGroupFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zlc
 */
public class MainActivity extends AppCompatActivity {

    private RadioGroup mRgMain;
    private ViewFragment mFragment1;
    private ViewGroupFragment mFragment2;
    private List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initListener();
    }

    private void initListener() {
        mRgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.rb_view:
                        showIndexFragment(0);
                        break;
                    case R.id.rb_viewgroup:
                        showIndexFragment(1);
                        break;
                }
            }
        });
    }

    private void initView() {

        mRgMain = (RadioGroup) findViewById(R.id.rg_main);

    }

    private void initData(){
        mFragments = new ArrayList<>();
        mFragments.add( mFragment1 = new ViewFragment());
        mFragments.add( mFragment2 = new ViewGroupFragment());
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_main,mFragment1)
                .add(R.id.fl_main,mFragment2)
                .show(mFragment1)
                .hide(mFragment2)
                .commit();
    }

    public void showIndexFragment(int index){
        getSupportFragmentManager()
                .beginTransaction()
                .show(mFragments.get(index))
                .hide(mFragments.get(mFragments.size() - 1 - index))
                .commit();
    }
}
