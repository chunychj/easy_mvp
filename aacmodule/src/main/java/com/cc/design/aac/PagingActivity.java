package com.cc.design.aac;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.DataSource;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @author admin
 * @类描述：
 * @作者： zhenglecheng
 * @创建时间： 2019/10/23 18:15
 */
public class PagingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paging);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyPagingAdapter adapter = new MyPagingAdapter(this, new DiffUtil.ItemCallback<List<String>>() {

            @Override
            public boolean areItemsTheSame(@NonNull List<String> oldItem, @NonNull List<String> newItem) {
                return false;
            }

            @Override
            public boolean areContentsTheSame(@NonNull List<String> oldItem, @NonNull List<String> newItem) {
                return false;
            }
        });
        recyclerView.setAdapter(adapter);

        PagedList.Config config = new PagedList.Config.Builder().setPageSize(50)
                .setPrefetchDistance(150)
                .setEnablePlaceholders(true)
                .build();

        DataSource.Factory factory = new DataSource.Factory() {
            @NonNull
            @Override
            public DataSource create() {
                return null;
            }
        };
//        LiveData liveData = new LivePagedListBuilder(factory, config).setFetchExecutor()
//                .build();

        ContactViewModel viewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
        viewModel.getContacts().observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {

            }
        });
    }

}
