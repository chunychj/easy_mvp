package com.eflagcomm.emvp.ui;

import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.eflagcomm.emvp.R;
import com.eflagcomm.emvp.ui.fragment.ViewFragment;
import com.eflagcomm.emvp.ui.fragment.ViewGroupFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 自定义View Activity
 */
public class WidgetActivity extends AppCompatActivity {

    private RadioGroup mRgMain;
    private ViewFragment mFragment1;
    private ViewGroupFragment mFragment2;
    private List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);

        initView();
        initData();
        initListener();
    }

    private void initListener() {
        mRgMain.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rb_view:
                    showIndexFragment(0);
                    break;
                case R.id.rb_viewgroup:
                    showIndexFragment(1);
                    break;
                default:
            }
        });
    }

    private void initView() {

        mRgMain = (RadioGroup) findViewById(R.id.rg_main);

    }

    private void initData() {
        mFragments = new ArrayList<>();
        mFragments.add(mFragment1 = new ViewFragment());
        mFragments.add(mFragment2 = new ViewGroupFragment());
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_main, mFragment1)
                .add(R.id.fl_main, mFragment2)
                .show(mFragment1)
                .hide(mFragment2)
                .commit();
    }

    public void showIndexFragment(int index) {
        getSupportFragmentManager()
                .beginTransaction()
                .show(mFragments.get(index))
                .hide(mFragments.get(mFragments.size() - 1 - index))
                .commit();
    }
}
