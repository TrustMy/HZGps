package com.sy.hzgps.gps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.sy.hzgps.ApkConfig;
import com.sy.hzgps.Config;
import com.sy.hzgps.MyContext;
import com.sy.hzgps.bean.ShowGpsBean;
import com.sy.hzgps.database.DBManagerLH;
import com.sy.hzgps.message.CommonMessage;
import com.sy.hzgps.message.ObdMessageID;
import com.sy.hzgps.message.ca.CAAlarmReportMessage;
import com.sy.hzgps.message.ca.CALocationReportMessage;
import com.sy.hzgps.tool.lh.GaoToGps;
import com.sy.hzgps.tool.lh.L;
import com.sy.hzgps.tool.lh.T;
import com.sy.hzgps.tool.lh.TimeTool;
import com.sy.hzgps.tool.sy.EngineStatus;

import java.util.HashMap;

/**
 * Created by Trust on 2017/4/24.
 */
public class GapGpsHelper extends GpsHelper implements Runnable{

    private Context context = MyContext.getContext();
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    private static boolean isWorking = false;

    private Handler commHandler = null;

    private int Status = 3, gpsStatusNum = -1;

    private double Distance;

    private String Place = "";

    private GaoToGps gaoToGps;

    private boolean IsStopGps = false;

    public void setCommHandler(Handler handler) {
        this.commHandler = handler;
    }

    private int maxSpeed = 0;

    private int Num = 0;

    private int chaosu = 1;

    private int zhengchang = 1;

    private int GpsLoactionMaxTime = 0;

    private boolean isOverSpeed = false;

    private Handler handler = null;

    public Handler getHandler() {
        return handler;
    }

    public static Handler mainHandler;

    public static boolean gpsStarted = false;

    private DBManagerLH dbManagerLH;

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {

            if(aMapLocation != null){
                gpsFixed = true;
            }else{
                gpsFixed = false;
            }

            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                L.d("gps 定位成功了 ");

                L.d("坐标:"+aMapLocation.getLatitude()+"|long:"+aMapLocation.getLongitude());



                if(ApkConfig.Time == aMapLocation.getTime()){
                    L.d("gps  缓存!");
                }else{
                    L.d("gps  坐标更新");
                    ApkConfig.Time = aMapLocation.getTime();


                    //如何 apkConfig time 为0时赋予gps时间
                    if(ApkConfig.startTime == 0){
                        ApkConfig.startTime = ApkConfig.Time;
                    }


                    L.d("ApkConfig.startTime :"+ApkConfig.startTime);
                    //如何apkConfig.starttime == 0 结束时间去当前系统时间
                    if(ApkConfig.startTime == 0){
                        ApkConfig.startTime = TimeTool.getSystemTimeDate();
                    }else{
                        //如何开始时间不为0  结束时间去gps 时间
                        ApkConfig.endTime = ApkConfig.Time;
                        ApkConfig.generatePictureTime = ApkConfig.endTime;
                    }

                    L.d(" ApkConfig.endTime:"+ ApkConfig.endTime+"|generatePictureTime:"+ApkConfig.generatePictureTime );








                    Bundle data = new Bundle();
                    float speed = aMapLocation.getSpeed() * 3.6f;   //转成 公里/时


                    ++zhengchang;

                    HashMap<String, Double> hm = gaoToGps.delta(aMapLocation.getLatitude(),
                            aMapLocation.getLongitude());


                    if (speed >= maxSpeed) {

                        ++chaosu;
                        Log.v("lhh", "GpsHelper chaosu" + chaosu);
                        if (chaosu >= 5) {


                            L.d("---- Report OverSpeed Alarm ---");

                            // 生成 超速报警 消息
                            CAAlarmReportMessage alarmMessage = new CAAlarmReportMessage(ObdMessageID.OVER_SPEED_ALARM, true);

                            //alarmMessage.setGpsTime(System.currentTimeMillis() - 18000);
                            alarmMessage.setGpsTime(ApkConfig.Time);

                            //
                            alarmMessage.setFixed(gpsFixed);
                            alarmMessage.setLat(hm.get("lat"));
                            alarmMessage.setLng(hm.get("lon"));
                            alarmMessage.setAlt(aMapLocation.getAltitude());
                            alarmMessage.setGpsSpeed(speed);
                            alarmMessage.setBearing(aMapLocation.getBearing());


                            alarmMessage.setEngineStatus(EngineStatus.ACC_ON.value());

                    /*
                    //播放报警信息
                    mp.start();
                    Num++;
                    isOverSpeed = true;
                    chaosu = 0;
                    */
                            data.putSerializable("location", alarmMessage);

                            Message message = Message.obtain();

                            message.setData(data);
                            message.what = CommonMessage.MSG_LOCATION_REPORT;

                            commHandler.sendMessage(message);

                            Log.v("lhh", "报警数据发送 success");

                        }/* else {
                    isOverSpeed = false;
                }*/

                    } else {

                        isOverSpeed = false;
                        chaosu = 0;
                    }

                    if (zhengchang >= 5) {

                        L.d("---- Report GPS Data ---");

                        // 生成 位置汇报 消息
                        CALocationReportMessage lrpMessage = new CALocationReportMessage(ObdMessageID.OBD_REPORT, true);

                        lrpMessage.setGpsTime(ApkConfig.Time);

                        L.d("gps location lrpMessage gps time :"+lrpMessage.getGpsTime()
                        +"|getTimeStamp :"+lrpMessage.getTimeStamp());


                        lrpMessage.setFixed(gpsFixed);


                        lrpMessage.setLat(hm.get("lat"));
                        lrpMessage.setLng(hm.get("lon"));
                        lrpMessage.setAlt(aMapLocation.getAltitude());
                        lrpMessage.setGpsSpeed(speed);
                        lrpMessage.setBearing(aMapLocation.getBearing());

                        L.d("getLocationType :"+aMapLocation.getLocationType());
                        L.d("aMapLocation time:"+aMapLocation.getTime()+"aMapLocation lat:"+aMapLocation.getLatitude()+"|aMapLocation lon:"+aMapLocation.getLongitude());
                        T.showToast(context,"getLocationType:"+aMapLocation.getLocationType());
                        ApkConfig.updateLat = hm.get("lat");
                        ApkConfig.updateLon = hm.get("lon");


                        lrpMessage.setEngineStatus(EngineStatus.ACC_ON.value());


                        data.putSerializable("location", lrpMessage);

                        zhengchang = 0;

                        Message message2 = Message.obtain();
                        message2.setData(data);
                        message2.what = CommonMessage.MSG_LOCATION_REPORT;

                        commHandler.sendMessage(message2);

                        L.d("lrpMessage: lat:"+lrpMessage.getLat()+"|long:"+lrpMessage.getLng()+"|" +
                                "alt:"+lrpMessage.getAlt()+"|bear:"+lrpMessage.getBearing()
                                +"|speed:"+lrpMessage.getGpsSpeed()+"|time:"+
                                lrpMessage.getGpsTime());
                    }


                    Message message = new Message();
                    message.what = Config.GPS;
                    message.obj = new ShowGpsBean(aMapLocation.getLatitude(),aMapLocation.getLongitude()
                            ,speed,isOverSpeed,aMapLocation.getTime());
                    mainHandler.sendMessage(message);
                }



            } else {
                L.err("错误:"+aMapLocation.getErrorCode());
            }
        }

    };

    public void startGps(){
        L.d("gpsStarted:"+gpsStarted);
        if (!gpsStarted) {

            L.d("启动定位");
            //启动定位
            mLocationClient.startLocation();
            gpsStarted = true;
        }
    }

    public void stopGps(){
        if (gpsStarted) {
            L.d("stop gps");
            isWorking = false;

            chaosu = 0;

            Num = 0;

            GpsLoactionMaxTime = 0;

            gpsStatusNum = -1;

            Distance = -100;//  don't stopgps
            Place = "";//地点


            gpsStarted = false;

            //停止
            mLocationClient.stopLocation();
        }else{
            L.d("stop gps no ");

        }
    }

    public void init (){
        gaoToGps = new GaoToGps();

        handler = new GpsHandler(this);

        // 读取超速报警阈值
        SharedPreferences prefs = context.getSharedPreferences("CommParams", Context.MODE_PRIVATE);

        maxSpeed = prefs.getInt("maxSpeed", 1000);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("maxSpeed", maxSpeed);

        editor.commit();

        dbManagerLH = new DBManagerLH(context);
        initGpsManagers();

    }

    private void initGpsManagers() {
        //初始化定位
        mLocationClient = new AMapLocationClient(context);


        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(
                AMapLocationClientOption.AMapLocationMode.Device_Sensors); //仅gps 定位
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(2*1000);
        mLocationOption.setHttpTimeOut(20000); //设置定位最大时间
        mLocationOption.setWifiScan(true);

        mLocationOption.setOnceLocation(false);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //启动定位
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

        L.d("Gps init success ");
    }


    @Override
    public void run() {
        Looper.prepare();
        init();
        Looper.loop();

    }




}
