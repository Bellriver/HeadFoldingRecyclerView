package com.rivers.headFoldingRecyclerView.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.rivers.headFoldingRecyclerView.R;
import com.rivers.headFoldingRecyclerView.adapter.RecyclerAdapter;
import com.rivers.headFoldingRecyclerView.model.ModelData;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        RecyclerView recyclerView=findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,RecyclerView.VERTICAL));
        ArrayList<ModelData> data = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            data.add(new ModelData("条目"+i,"布局","这是条目"+i+"的正文",System.currentTimeMillis()));
        }
        RecyclerAdapter adapter = new RecyclerAdapter(this,data);
        recyclerView.setAdapter(adapter);
    }
}
