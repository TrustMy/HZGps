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
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.sy.hzgps.bean.OrderBean;
import com.sy.hzgps.bean.QRcodeBean;
import com.sy.hzgps.bean.ShowGpsBean;
import com.sy.hzgps.database.DBHelperLH;
import com.sy.hzgps.database.DBManagerLH;
import com.sy.hzgps.getdata.GetDataActivity;
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
import com.sy.hzgps.tool.qrcode.QRcodeTool;

import org.json.JSONObject;

import java.text.DecimalFormat;
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

    private ImageButton start, end;

    private static int timeMinute = 0;

    private String startName, endName, time, order, IMEI, termIdstring;

    private int termId;

    private Bitmap qR;

    private SharedPreferences pres;

    private PostRequest postRequest;

    private long startTime, endTime, generatePictureTime;

    public Handler dataHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Config.GPS:
                    if (msg.obj != null) {
                        DecimalFormat df = new DecimalFormat("#0.0");
                        ShowGpsBean showGpsBean = (ShowGpsBean) msg.obj;
                        gpsMsg.setText(df.format(showGpsBean.getSpeed()) + "");

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
                    L.d("orderBean.getStatus():" + orderBean.getStatus());
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
            map.put("startTime", startTime + "");
            map.put("endTime", endTime + "");
            map.put("generatePictureTime", generatePictureTime + "");
            JSONObject jsonObject = new JSONObject(map);

            return jsonObject.toString();
        } else {
            return null;
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AndroidCheckVersion(this).checkVersion();
        init();
        initView();

        if (myServer == null) {
            bindService(new Intent(MainActivity.this, MyService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }

        MyService.mainHandler = dataHandler;
        GpsHelper.mainHandler = dataHandler;

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
     * 开始工作
     *
     * @param v
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void startGps(View v) {
        startName = startLocationEd.getText().toString().trim();
        endName = endLocationEd.getText().toString().trim();
        if (!endName.equals("") && !startName.equals("")) {

            start.setVisibility(View.GONE);
            end.setVisibility(View.VISIBLE);

            logo.setImageResource(R.drawable.truck_on);
            promptTv.setVisibility(View.VISIBLE);
            promptWorkTv.setVisibility(View.VISIBLE);

            startLocationEd.setEnabled(false);
            endLocationEd.setEnabled(false);

            myServer.startWorking();

            startTime = TimeTool.getSystemTimeDate();
            timeTv.setText("0");
        } else {
            T.showToast(this, "起点或终点输入有误!");
        }
    }

    /**
     * 结束
     *
     * @param v
     */
    public void endGps(View v) {

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
                endTime = TimeTool.getSystemTimeDate();
                generatePictureTime = endTime;
                closeGps();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void closeGps() {
        start.setVisibility(View.VISIBLE);
        end.setVisibility(View.GONE);

        logo.setImageResource(R.drawable.truck);
        promptTv.setVisibility(View.GONE);
        promptWorkTv.setVisibility(View.GONE);

        startLocationEd.setEnabled(true);
        endLocationEd.setEnabled(true);

        myServer.stopWorking();
        setQRcode();

        timeMinute = 0;
        timeTv.setText("---------");


    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String s = getIntent().getStringExtra("test");
        if (s != null) {
            workMsg.setText(s);
        }
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            String t = data.getStringExtra("startTime");

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
            time = TimeTool.getSystemTime();


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
        Message message = Message.obtain();
        message.what = Config.SAVE_HISTORY;
        message.obj = new OrderBean(order, startName, endName, time, qR, termId, status, startTime,
                endTime, generatePictureTime);
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
        map.put("startTime", startTime);
        map.put("endAddress", endName);
        map.put("endTime", endTime);
        map.put("generatePictureTime", generatePictureTime);
        map.put("permission", 0);
        map.put("pictureStr", BitmapAndStringUtils.convertIconToString(qR));

        postRequest.requestOrder(Server.Server + Server.Order, map, Config.ORDER);
    }

    @Override
    public void onClick(View view) {
        /**
         * 设置edittext 可编辑和不可编辑
         */
        switch (view.getId()) {

        }
    }


}
