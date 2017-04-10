package com.sy.hzgps.getdata;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;


import com.sy.hzgps.BaseActivity;
import com.sy.hzgps.MainActivity;
import com.sy.hzgps.R;
import com.sy.hzgps.tool.lh.RecyclerViewDivider;

import java.util.ArrayList;
import java.util.List;


public class GetDataActivity extends BaseActivity   {
    private RecyclerView dataRecyclerView;
    private GetDataRecyclerAdapter getDataRecyclerAdapter;
    private List<String> ml = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_data);
        initView();
        initDate();
    }

    private void initDate() {
        for (int i = 0; i < 10 ; i++) {
            ml.add("this is  work "+i);
        }
        getDataRecyclerAdapter.setMl(ml);
        getDataRecyclerAdapter.notifyDataSetChanged();
    }

    private void initView() {
        dataRecyclerView = findView(R.id.getdate_recycler);
        getDataRecyclerAdapter = new GetDataRecyclerAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        dataRecyclerView.setLayoutManager(linearLayoutManager);
        dataRecyclerView.setAdapter(getDataRecyclerAdapter);

        dataRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL));
        getDataRecyclerAdapter.t = new GetDataRecyclerAdapter.test() {

            @Override
            public void click(View v, String msg) {
                Toast.makeText(GetDataActivity.this,"t :"+msg,Toast.LENGTH_LONG).show();
                setResult(1,new Intent(GetDataActivity.this, MainActivity.class).putExtra("startTime",msg));
                finish();
            }
        };
    }
}
