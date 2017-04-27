package com.sy.hzgps.getdata;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;


import com.sy.hzgps.ApkConfig;
import com.sy.hzgps.BaseActivity;
import com.sy.hzgps.Config;
import com.sy.hzgps.MainActivity;
import com.sy.hzgps.R;
import com.sy.hzgps.bean.OrderBean;
import com.sy.hzgps.database.DBManagerLH;
import com.sy.hzgps.request.PostRequest;
import com.sy.hzgps.tool.Server;
import com.sy.hzgps.tool.dialog.DialogTool;
import com.sy.hzgps.tool.lh.BitmapAndStringUtils;
import com.sy.hzgps.tool.lh.L;
import com.sy.hzgps.tool.lh.RecyclerViewDivider;
import com.sy.hzgps.tool.lh.T;
import com.sy.hzgps.tool.lh.TimeTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;


public class GetDataActivity extends BaseActivity   {
    private RecyclerView dataRecyclerView;
    private GetDataRecyclerAdapter getDataRecyclerAdapter;
    private List<OrderBean> ml = new ArrayList<>();
    private DBManagerLH dbManagerLH ;
    private static OrderBean mOrderBean;
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
        getDataRecyclerAdapter = new GetDataRecyclerAdapter(GetDataActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        dataRecyclerView.setLayoutManager(linearLayoutManager);
        dataRecyclerView.setAdapter(getDataRecyclerAdapter);

//        dataRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL));
        getDataRecyclerAdapter.t = new GetDataRecyclerAdapter.test() {

            @Override
            public void click(View v, OrderBean orderBean) {

                switch (v.getId()){

                    case R.id.get_recycler_item_submit:

                        SavaToHttp(orderBean,ApkConfig.PhotoBitMap);
                        ApkConfig.PhotoBitMap = null;
                        break;

                    case R.id.get_date_item_update_photo_btn:
                        mOrderBean = orderBean;
                        BitmapAndStringUtils.getPhoto(GetDataActivity.this,BitmapAndStringUtils.getImgFile(ApkConfig.flieName));
                        break;
                }



            }
        };
    }

    /**
     * 上传订单消息
     * @param orderBean
     */
    private void SavaToHttp(OrderBean orderBean,Bitmap bitmap) {
        L.d("orderBean:"+orderBean.getOrderPhotoBit());
        T.showToast(GetDataActivity.this,"提交订单中...");

        Map<String,Object> map = new WeakHashMap<>();
        order = orderBean.getOrder();
        L.d("startTime:"+orderBean.getStartTime()+"|endTime:"+orderBean.getEndTime()+"|" +
                "generatePictureTime:"+orderBean.getGeneratePictureTime());
        map.put("orderNo",orderBean.getOrder());
        map.put("driverId",orderBean.getTermId());
        map.put("startAddress",orderBean.getStartName());
        map.put("startTime",orderBean.getStartTime());
        map.put("endAddress",orderBean.getEndName());
        map.put("endTime",orderBean.getEndTime());
        map.put("generatePictureTime",orderBean.getGeneratePictureTime());
        map.put("permission",0);
        if (orderBean.getqR() != null) {
            map.put("pictureStr", BitmapAndStringUtils.
                    convertIconToString(orderBean.getqR()));
        }
        if(bitmap == null){
            map.put("orderPic",orderBean.getOrderPhotoBit());
        }else{
            map.put("orderPic",BitmapAndStringUtils.
                    convertIconToString(bitmap));
        }

        postRequest.requestOrder(Server.Server+Server.Order,map, Config.ORDER);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (BitmapAndStringUtils.getImgFile(ApkConfig.flieName).exists() && requestCode == ApkConfig.PhoneCode) {

                /*
                String path = getImgFile().getPath();
                fis = new FileInputStream(path);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                L.d("onActivityResult: bitmap1:"+bitmap.toString()
                        +"|bitmap 大小:"+(bitmap.getByteCount() / 1024 / 1024)+"m");
                */
            ApkConfig.PhotoBitMap = BitmapAndStringUtils.bitmapCompression(ApkConfig.
                    flieName,ApkConfig.fliePath);
            if(ApkConfig.PhotoBitMap != null){
//                    logo.setImageBitmap(ApkConfig.PhotoBitMap);
                String time;
                if(ApkConfig.endTime == 0){
                    time = TimeTool.getSystemTime();
                }else{
                    time = TimeTool.getGPSTime(ApkConfig.endTime);
                }
                DialogTool.showPhotoDialog(GetDataActivity.this,R.layout.dialog_photo,ApkConfig.
                        PhotoBitMap, time);
                DialogTool.phoneOnClick = new DialogTool.PhoneOnClick() {
                    @Override
                    public void onClick(View v) {
                        DialogTool.photoDialog.dismiss();
                        SavaToHttp(mOrderBean,ApkConfig.PhotoBitMap);
                    }
                };
            }else{
                L.err("photoBitmap  is  null!");
            }


        }
    }
}
