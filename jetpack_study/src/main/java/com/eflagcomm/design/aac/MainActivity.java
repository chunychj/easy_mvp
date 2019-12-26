package com.eflagcomm.design.aac;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv).setOnClickListener(v -> startActivity(LifeCyclerActivity.class));
        findViewById(R.id.room).setOnClickListener(v -> startActivity(RoomActivity.class));
        findViewById(R.id.con_layout).setOnClickListener(v -> startActivity(ConstraintLayoutActivity.class));
        findViewById(R.id.navigation).setOnClickListener(v -> startActivity(NavigationActivity.class));
        findViewById(R.id.paging).setOnClickListener(v -> startActivity(PagingActivity.class));
    }

    private void startActivity(Class clazz) {
        Intent intent = new Intent(MainActivity.this, clazz);
        startActivity(intent);
    }
}
