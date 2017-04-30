package com.sy.hzgps;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.google.zxing.WriterException;
import com.sy.hzgps.bean.GpsBean;
import com.sy.hzgps.bean.OrderBean;
import com.sy.hzgps.bean.QRcodeBean;
import com.sy.hzgps.bean.ShowGpsBean;
import com.sy.hzgps.database.DBHelperLH;
import com.sy.hzgps.database.DBManagerLH;
import com.sy.hzgps.getdata.GetDataActivity;
import com.sy.hzgps.gps.GapGpsHelper;
import com.sy.hzgps.gps.GpsHelper;
import com.sy.hzgps.logregist.LoginActivity;
import com.sy.hzgps.message.ObdMessage;
import com.sy.hzgps.request.PostNet;
import com.sy.hzgps.request.PostRequest;
import com.sy.hzgps.server.MyService;
import com.sy.hzgps.tool.GenerateSequence;
import com.sy.hzgps.tool.Server;
import com.sy.hzgps.tool.lh.AndroidCheckVersion;
import com.sy.hzgps.tool.lh.BitmapAndStringUtils;
import com.sy.hzgps.tool.lh.L;
import com.sy.hzgps.tool.lh.T;
import com.sy.hzgps.tool.lh.TimeTool;
import com.sy.hzgps.tool.dialog.DialogTool;
import com.sy.hzgps.tool.lh.gps.CoordinateTransformation;
import com.sy.hzgps.tool.lh.gps.GPSHistoryLine;
import com.sy.hzgps.tool.qrcode.QRcodeTool;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class MainActivity extends BaseActivity {
    private MyService myServer = null;
    private ImageView logo;
    private TextView workMsg, gpsMsg, timeTv;
    private TextView promptTv, promptWorkTv;
    private DBHelperLH dbHelperLH;
    private DBManagerLH dbManagerLH;

    private EditText startLocationEd, endLocationEd;

    private LinearLayout endNameEdLayout;

    private ImageButton start, end;

    private static int timeMinute = 0;

    private String startName, endName, time, order, IMEI, termIdstring;

    private int termId;

    private Bitmap qR;

    private SharedPreferences pres;

    private PostRequest postRequest;

    private MapView mMapView;
    private AMap aMap;
    private CoordinateTransformation coordinateTransformation;
    public Handler dataHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Config.GPS:
                    if (msg.obj != null) {
                        DecimalFormat df = new DecimalFormat("#0.0");
                        ShowGpsBean showGpsBean = (ShowGpsBean) msg.obj;
                        gpsMsg.setText(df.format(showGpsBean.getSpeed()) + "");

                        shwoGps(showGpsBean);


                    }
                    break;
                case Config.TIME:
                    timeMinute++;
                    timeTv.setText(timeMinute + "");
                    L.d("timeMinute:" + timeMinute);
                    break;

                case Config.SAVE_HISTORY:
                    OrderBean orderBean = (OrderBean) msg.obj;
                    Map<String, Object> map = new WeakHashMap<>();
                    map.put("termId", orderBean.getTermId());
                    map.put("order", orderBean.getOrder());
                    map.put("startName", orderBean.getStartName());
                    map.put("endName", orderBean.getEndName());
                    map.put("qR", orderBean.getqR());
                    map.put("time", orderBean.getTime());
                    map.put("startTime", orderBean.getStartTime() + "");
                    map.put("endTime", orderBean.getEndTime() + "");
                    map.put("generatePictureTime", orderBean.getGeneratePictureTime() + "");
                    map.put("status", orderBean.getStatus());
                    map.put("orderPhotoBit",orderBean.getOrderPhotoBit());
                    L.d("orderBean.getStatus():" + orderBean.getStatus());
                    L.d("Config.SAVE_HISTORY startTime:"+ orderBean.getStartTime());
                    dbManagerLH.addOrder(map);
                    break;

                case Config.ORDER:
                    if (msg.arg1 == Config.RESULT_SUCCESS) {
                        T.showToast(MainActivity.this, " 提交订单成功!");
                        saveData(Config.SAVE_STATUS_SUCCESS);
                    } else {
                        saveData(Config.SAVE_STATUS_ERROR);
                        T.showToast(MainActivity.this, " 提交订单失败!请在工作表中重新提交!");
                    }


                    break;
            }
        }
    };




    private void shwoGps(ShowGpsBean showGpsBean) {
        LatLng gps = new LatLng(showGpsBean.getLat(),showGpsBean.getLon());
        aMap.clear();
        aMap.addMarker(new MarkerOptions().
                position(gps).
                title("当前位置\n"+TimeTool.getGPSTime(showGpsBean.getTime())))
                .showInfoWindow();

        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                gps,//新的中心点坐标
                500, //新的缩放级别
                0, //俯仰角0°~45°（垂直与地图时为0）
                0  ////偏航角 0~360° (正北方为0)
        )));
    }

    /**
     * 设置二维码生成的内容
     *
     * @param time
     * @return
     */
    public String getMsg(String time) {
        if (!startName.equals("") && !endName.equals("")) {

            Map<String, Object> map = new WeakHashMap<String, Object>();
            order = GenerateSequence.generateSequenceNo() + IMEI;
            map.put("order", order);
            map.put("termId", termId);
            map.put("startName", startName);
            map.put("endName", endName);
            map.put("time", time);
            if(ApkConfig.startTime == 0){
                map.put("startTime", TimeTool.getSystemTimeDate() + "");
            }else{
                map.put("startTime", ApkConfig.startTime + "");
            }
            if(ApkConfig.endTime == 0){
                map.put("endTime", TimeTool.getSystemTimeDate() + "");
            }else{
                map.put("endTime", ApkConfig.endTime + "");
            }
            if(ApkConfig.generatePictureTime == 0){
                map.put("generatePictureTime", TimeTool.getSystemTimeDate() + "");
            }else{
                map.put("generatePictureTime", ApkConfig.generatePictureTime + "");
            }


            JSONObject jsonObject = new JSONObject(map);
            L.d("getMsg starttime:"+ApkConfig.startTime);
            return jsonObject.toString();
        } else {
            L.d("null");
            return null;
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        coordinateTransformation = new CoordinateTransformation(MainActivity.this);


        new AndroidCheckVersion(this).checkVersion();
        init();
        initView();

        if (myServer == null) {
            bindService(new Intent(MainActivity.this, MyService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }

        MyService.mainHandler = dataHandler;
        GapGpsHelper.mainHandler = dataHandler;

        pres = MainActivity.this.getSharedPreferences("CommParams", Activity.MODE_PRIVATE);
        IMEI = pres.getString("terminalId", null);
        termIdstring = pres.getString("license", null);
        termId = Integer.parseInt(termIdstring);
        if (IMEI == null || termIdstring == null) {
            T.showToast(MainActivity.this, "请插入sim卡!或账号为null");
        }

    }

    /**
     * 初始化网络操作
     */
    private void init() {
        postRequest = new PostRequest(this, dataHandler);
    }


    private void initView() {
        dbHelperLH = new DBHelperLH(this);
        dbHelperLH.getWritableDatabase();
        dbManagerLH = new DBManagerLH(this);

        workMsg = findView(R.id.main_work_msg);
        gpsMsg = findView(R.id.main_gps);

        startLocationEd = findView(R.id.main_start_location_ed);
        endLocationEd = findView(R.id.main_end_location_ed);

        logo = findView(R.id.main_logo);

        start = findView(R.id.main_start_gps);
        end = findView(R.id.main_end_gps);

        promptTv = findView(R.id.main_prompt);
        promptWorkTv = findView(R.id.main_prompt_work);

        timeTv = findView(R.id.main_time);

        endNameEdLayout = findView(R.id.main_end_location_ed_layout);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            myServer = ((MyService.MsgBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            myServer = null;
        }
    };

    /**
     * 确认开始工作
     *
     * @param v
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void startGps(View v) {
        startName = startLocationEd.getText().toString().trim();
        if(!startName.equals("")){
            BitmapAndStringUtils.getPhoto(this,BitmapAndStringUtils.getImgFile(ApkConfig.flieName));
        }else{
            T.showToast(MainActivity.this,"请输入起点!");
        }



    }

    /**
     *   开始处理gps数据
     */
    private void startGps() {



            start.setVisibility(View.GONE);
            end.setVisibility(View.VISIBLE);

            logo.setImageResource(R.drawable.truck_on);
//            promptTv.setVisibility(View.VISIBLE);
            promptWorkTv.setVisibility(View.VISIBLE);

            startLocationEd.setEnabled(false);

//            endLocationEd.setEnabled(false);

            myServer.startWorking();

            timeTv.setText("0");

    }

    /**
     * 确认结束
     *
     * @param v
     */
    public void endGps(View v) {
        endNameEdLayout.setVisibility(View.VISIBLE);
        endName = endLocationEd.getText().toString().trim();
        if(!endName.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("本次行程结束?");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    closeGps();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            T.showToast(MainActivity.this,"请输入终点,在点击结束!");
        }



    }

    /***
     *  停止gps
     */
    private void closeGps() {
        start.setVisibility(View.VISIBLE);
        end.setVisibility(View.GONE);

        logo.setImageResource(R.drawable.truck);
        promptTv.setVisibility(View.GONE);
        promptWorkTv.setVisibility(View.GONE);

        startLocationEd.setEnabled(true);
        endLocationEd.setEnabled(true);


        setQRcode();
        myServer.stopWorking();

        timeMinute = 0;
        timeTv.setText("---------");
        endLocationEd.setText("");
        endNameEdLayout.setVisibility(View.GONE);


        ApkConfig.startTime = 0 ;
        ApkConfig.endTime = 0;
        ApkConfig.generatePictureTime = 0;
    }







    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
        mMapView.onDestroy();
    }


    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，
        // 保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String s = getIntent().getStringExtra("test");
        if (s != null) {
            workMsg.setText(s);
        }
        mMapView.onResume();
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
                    DialogTool.showPhotoDialog(MainActivity.this,R.layout.dialog_photo,ApkConfig.
                            PhotoBitMap, time);
                    DialogTool.phoneOnClick = new DialogTool.PhoneOnClick() {
                        @Override
                        public void onClick(View v) {
                            DialogTool.photoDialog.dismiss();
                            startGps();
                        }
                    };
                }else{
                    L.err("photoBitmap  is  null!");
                }



        }


        super.onActivityResult(requestCode, resultCode, data);
    }



                @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * menu
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_register:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.main_erweima:
                List<QRcodeBean> qRcodeBeen = dbManagerLH.select();

                if (qRcodeBeen.size() == 0) {
                    Toast.makeText(this, "未找到二维码,请确认是否生成!", Toast.LENGTH_LONG).show();
                } else {
//                    test.setImageBitmap(qRcodeBeen.get(0).getBitmap());
                    DialogTool.showDialog(this, R.layout.dialog_qr, qRcodeBeen.get(0)
                            .getBitmap(), qRcodeBeen.get(0).getTime());
                }
                break;
            case R.id.main_getdata:
//                startActivity(new Intent(MainActivity.this, GetDataActivity.class));
                startActivityForResult(new Intent(MainActivity.this, GetDataActivity.class), 1);

                break;

        }
        return true;
    }

    /**
     * 生成二维码
     */
    private void setQRcode() {
        try {
            if(ApkConfig.endTime == 0){
                time = TimeTool.getSystemTime();
            }else{
                time = TimeTool.getGPSTime(ApkConfig.endTime);
            }


            qR = QRcodeTool.getQRcode(getMsg(time), 600);

            T.showToast(MainActivity.this,"提交订单中...");
            requestHttpDb();

            DialogTool.showDialog(this, R.layout.dialog_qr, qR, time);

            List<QRcodeBean> qRcodeBeens = dbManagerLH.select();
            if (qRcodeBeens != null && qRcodeBeens.size() != 0) {
                dbManagerLH.update(qR, time);
            } else {
                dbManagerLH.add(qR, time);
            }




        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "二维码生成错误", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 保存数据库
     *
     * @param status
     */
    private void saveData(int status) {
        long startTime , endTime , genratePictureTime;
        if(ApkConfig.startTime == 0 && ApkConfig.generatePictureTime == 0
                && ApkConfig.endTime == 0){
            startTime = TimeTool.getSystemTimeDate();
            endTime = startTime;
            genratePictureTime = endTime;
        }else{
            startTime = ApkConfig.startTime;
            endTime = ApkConfig.endTime;
            genratePictureTime = ApkConfig.generatePictureTime;
        }

        L.d("saveData startTime:"+startTime);
        Message message = Message.obtain();
        message.what = Config.SAVE_HISTORY;
        L.d("photo bit :"+BitmapAndStringUtils.
                convertIconToString(ApkConfig.PhotoBitMap));
        message.obj = new OrderBean(order, startName, endName, time, qR, termId, status,
                startTime, endTime, genratePictureTime,BitmapAndStringUtils.
                convertIconToString(ApkConfig.PhotoBitMap));
        L.d("Config.SAVE_STATUS_SUCCESS:" + Config.SAVE_STATUS_ERROR);
        dataHandler.sendMessageDelayed(message, 1);


    }

    /**
     * 保存网络数据库
     */
    private void requestHttpDb() {
        Map<String, Object> map = new WeakHashMap<>();
        map.put("orderNo", order);
        map.put("driverId", termId);
        map.put("startAddress", startName);
        long startTime , endTime , genratePictureTime;
        if(ApkConfig.startTime == 0 || ApkConfig.generatePictureTime == 0
                || ApkConfig.endTime == 0){
            startTime = TimeTool.getSystemTimeDate();
            endTime = startTime;
            genratePictureTime = endTime;
        }else{
            startTime = ApkConfig.startTime;
            endTime = ApkConfig.endTime;
            genratePictureTime = ApkConfig.generatePictureTime;
        }
        L.d("requestHttpDb startTime:"+startTime);
        if(qR!= null){
        map.put("startTime", startTime);
        map.put("endAddress", endName);
        map.put("endTime", endTime);
        map.put("generatePictureTime", genratePictureTime);
        map.put("permission", 0);
        map.put("pictureStr", BitmapAndStringUtils.convertIconToString(qR));
        map.put("orderPic",BitmapAndStringUtils.convertIconToString(ApkConfig.PhotoBitMap));

        postRequest.requestOrder(Server.Server + Server.Order, map, Config.ORDER);
        }else{
            T.showToast(MainActivity.this,"二维码不能为空!");
        }
    }

    @Override
    public void onClick(View view) {
        /**
         * 设置edittext 可编辑和不可编辑
         */
        switch (view.getId()) {

        }
    }


    public void selecGps(View v){
        List<GpsBean> mGps =  dbManagerLH.selectGps();
        List<LatLng> ml = new ArrayList<>();
        if(mGps.size() == 0){
            T.showToast(MainActivity.this,"gps List :"+0);
        }else{
            T.showToast(MainActivity.this,"gps List :"+mGps.size());

            for (int i = 0; i < mGps.size(); i++) {
                L.d("selecGps bean :"+mGps.get(i).toString());
                if(mGps.get(i).getLat() == 0){
                    continue;
                }else{

                    ml.add(coordinateTransformation.transformation(new LatLng(mGps.get(i).getLat(),mGps.get(i).getLon())));
                }
            }


            GPSHistoryLine gpsHistoryLine = new GPSHistoryLine(aMap,MainActivity.this);
            gpsHistoryLine.setLatLngs(ml);
            gpsHistoryLine.startHistory("start","end");
        }
    }

    public void delGps(View v){
        aMap.clear();
        dbManagerLH.delGps();
    }


}
