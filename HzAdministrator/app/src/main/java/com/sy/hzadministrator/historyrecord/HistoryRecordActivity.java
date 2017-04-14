package com.sy.hzadministrator.historyrecord;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Switch;

import com.sy.hzadministrator.BaseActivity;
import com.sy.hzadministrator.Config;
import com.sy.hzadministrator.L;
import com.sy.hzadministrator.R;
import com.sy.hzadministrator.RecyclerViewDivider;
import com.sy.hzadministrator.Server;
import com.sy.hzadministrator.T;
import com.sy.hzadministrator.bean.RequestDataBean;
import com.sy.hzadministrator.db.DBManagerLH;
import com.sy.hzadministrator.db.OrderBean;
import com.sy.hzadministrator.request.PostRequest;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class HistoryRecordActivity extends BaseActivity {
    private DBManagerLH dbManagerLH;
    private RecyclerView historyRecordRecyclerView;
    private HistoryrecordAdapter historyRecordAdapter;
    private PostRequest postRequest;
    private String order;
    private Handler historyHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Config.ORDER:
                    if(msg.arg1 == Config.RESULT_SUCCESS){
                        RequestDataBean bean = (RequestDataBean) msg.obj;
                        if(bean.getConfirmStatus()){
                            T.showToast(HistoryRecordActivity.this,"提交成功,订单有效!");
                            dbManagerLH.updateOrder(order,Config.SAVE_STATUS_SUCCESS);
                            initData();

                        }else{
                            T.showToast(HistoryRecordActivity.this,"未找到相应订单,请确保司机上传!");
                        }

                    }else
                    {
                        T.showToast(HistoryRecordActivity.this,msg.obj.toString());

                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_record);
        initView();
        init();
    }

    private void initView() {
        historyRecordRecyclerView = findView(R.id.hzadministrator_recycler);
        historyRecordAdapter = new HistoryrecordAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        historyRecordRecyclerView.setLayoutManager(linearLayoutManager);
        historyRecordRecyclerView.setAdapter(historyRecordAdapter);
        historyRecordRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL));
        historyRecordAdapter.click = new HistoryrecordAdapter.Click() {
            @Override
            public void click(View v, OrderBean orderBean) {
                Map<String,Object> map = new WeakHashMap<>();
                order = orderBean.getOrder();
                map.put("orderNo",orderBean.getOrder());
                map.put("driverId",orderBean.getTermId());
                map.put("startAddress",orderBean.getStartName());
                map.put("startTime",orderBean.getStartTime());
                map.put("endAddress",orderBean.getEndName());
                map.put("endTime",orderBean.getEndTime());
                map.put("generatePictureTime",orderBean.getGeneratePictureTime());
                map.put("permission",1);
                postRequest.requestOrder(Server.Server+Server.Order,map, Config.ORDER);
            }
        };
    }

    private void init() {
        postRequest = new PostRequest(this,historyHandler);
        dbManagerLH = new DBManagerLH(this);
        initData();
    }

    private void initData() {
        List<OrderBean> ml = dbManagerLH.select();
        if(ml.size() == 0){

        }else{
            historyRecordAdapter.setMl(ml);
            historyRecordAdapter.notifyDataSetChanged();
        }
    }
}
