package com.sy.hzgps.getdata;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;


import com.sy.hzgps.BaseActivity;
import com.sy.hzgps.Config;
import com.sy.hzgps.MainActivity;
import com.sy.hzgps.R;
import com.sy.hzgps.bean.OrderBean;
import com.sy.hzgps.database.DBManagerLH;
import com.sy.hzgps.request.PostRequest;
import com.sy.hzgps.tool.Server;
import com.sy.hzgps.tool.lh.BitmapAndStringUtils;
import com.sy.hzgps.tool.lh.RecyclerViewDivider;
import com.sy.hzgps.tool.lh.T;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;


public class GetDataActivity extends BaseActivity   {
    private RecyclerView dataRecyclerView;
    private GetDataRecyclerAdapter getDataRecyclerAdapter;
    private List<OrderBean> ml = new ArrayList<>();
    private DBManagerLH dbManagerLH ;

    private PostRequest postRequest;
    private String order;
    private Handler getDataHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Config.ORDER:
                    if(msg.arg1 == Config.RESULT_SUCCESS){
                        dbManagerLH.updateOrder(order,Config.SAVE_STATUS_SUCCESS);
                        initDate();
                        T.showToast(GetDataActivity.this,"提交成功!");
                    }else{
                        T.showToast(GetDataActivity.this,"提交失败,请稍后再试!");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_data);
        init();
        initView();
        initDate();
    }

    private void init() {
        postRequest = new PostRequest(this,getDataHandler);
    }

    private void initDate() {
        List<OrderBean> ml = dbManagerLH.selectOrder();
        if(ml.size()!= 0){
            getDataRecyclerAdapter.setMl(ml);
            getDataRecyclerAdapter.notifyDataSetChanged();
        }else{
            T.showToast(this,"历史记录为空!");
        }
    }

    private void initView() {
        dbManagerLH = new DBManagerLH(this);
        dataRecyclerView = findView(R.id.getdate_recycler);
        getDataRecyclerAdapter = new GetDataRecyclerAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        dataRecyclerView.setLayoutManager(linearLayoutManager);
        dataRecyclerView.setAdapter(getDataRecyclerAdapter);

        dataRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL));
        getDataRecyclerAdapter.t = new GetDataRecyclerAdapter.test() {

            @Override
            public void click(View v, OrderBean orderBean) {
//                T.showToast(GetDataActivity.this,"点击了:"+orderBean.getOrder());
                T.showToast(GetDataActivity.this,"提交订单中...");

                Map<String,Object> map = new WeakHashMap<>();
                order = orderBean.getOrder();
                map.put("orderNo",orderBean.getOrder());
                map.put("driverId",orderBean.getTermId());
                map.put("startAddress",orderBean.getStartName());
                map.put("startTime",orderBean.getStartTime());
                map.put("endAddress",orderBean.getEndName());
                map.put("endTime",orderBean.getEndTime());
                map.put("generatePictureTime",orderBean.getGeneratePictureTime());
                map.put("permission",0);
                map.put("pictureStr", BitmapAndStringUtils.
                        convertIconToString(orderBean.getqR()));

                postRequest.requestOrder(Server.Server+Server.Order,map, Config.ORDER);
            }
        };
    }
}
