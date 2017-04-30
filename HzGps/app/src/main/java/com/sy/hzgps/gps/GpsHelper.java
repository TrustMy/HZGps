package com.sy.hzgps.gps;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;


import com.sy.hzgps.Config;
import com.sy.hzgps.MainActivity;
import com.sy.hzgps.bean.ShowGpsBean;
import com.sy.hzgps.tool.lh.L;
import com.sy.hzgps.tool.lh.T;
import com.sy.hzgps.tool.lh.TimeTool;
import com.sy.hzgps.tool.sy.EngineStatus;
import com.sy.hzgps.MyContext;
import com.sy.hzgps.message.CommonMessage;
import com.sy.hzgps.message.ObdMessageID;
import com.sy.hzgps.message.ca.CAAlarmReportMessage;
import com.sy.hzgps.message.ca.CALocationReportMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jiayang on 2016/7/26.
 */
public class GpsHelper   implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(GpsHelper.class);

    private static Context context = MyContext.getContext();

    private Handler handler = null;


    private static boolean isWorking = false;

    private Handler commHandler = null;

    private MediaPlayer mp;

    private int Status = 3, gpsStatusNum = -1;

    private double Distance;

    private String Place = "";

    private boolean IsStopGps = false;

    public void setCommHandler(Handler handler) {
        this.commHandler = handler;
    }

    private int maxSpeed = 0;

    private int Num = 0;

    private int chaosu = 1;

    private NotificationManager nm;

    private Notification notification;

    private Intent intenthhh;


    private int zhengchang = 1;


    private boolean isOverSpeed = false;

    private PendingIntent pendingIntent;


    private int GpsLoactionMaxTime = 0;

    public Handler getHandler() {
        return handler;
    }

    public static Handler mainHandler;

    /**
     * 初始化
     */
    public void init() {


        logger.info("GpsHelper init");


//        handler = new GpsHandler(this);

        // 读取超速报警阈值
        SharedPreferences prefs = context.getSharedPreferences("CommParams", Context.MODE_PRIVATE);

        maxSpeed = prefs.getInt("maxSpeed", 200);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("maxSpeed", maxSpeed);

        editor.commit();


        initGpsManager();

    }

    @Override
    public void run() {

        Looper.prepare();

        init();

        Looper.loop();

    }


    protected LocationManager locationMgr;
    protected Location gpsLocation = null;
    protected String gpsProvider;
    protected boolean gpsFixed = false;

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Status = status;
            switch (status) {
                // GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    logger.info("当前GPS状态为可见状态");
                    gpsFixed = true;
                    Log.i("lh", "onStatusChanged: GPS 正常工作");
                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    logger.info("当前GPS状态为服务区外状态");
                    gpsLocation = locationMgr.getLastKnownLocation(gpsProvider);
                    gpsFixed = false;
                    Log.i("lh", "onStatusChanged: GPS 服务区外状态");
                    break;
                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    logger.info("当前GPS状态为暂停服务状态");
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    gpsLocation = locationMgr.getLastKnownLocation(gpsProvider);
                    gpsFixed = false;
                    Log.i("lh", "onStatusChanged: GPS 暂停服务状态");
                    break;
            }

        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {

        }


        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onLocationChanged(Location location) {

            GpsStatus gpsStatus = locationMgr.getGpsStatus(null); // 获取当前状态
            // 获取默认最大卫星数
            int maxSatellites = gpsStatus.getMaxSatellites();

            Log.i("lh", "maxSatellites: " + maxSatellites);

            if (location != null) {
                gpsLocation = location;

                gpsFixed = true;

            } else {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                gpsLocation = locationMgr.getLastKnownLocation(gpsProvider);
                gpsFixed = false;
                Log.i("lh", "onLocationChanged: location ＝＝ null");
            }

            Bundle data = new Bundle();
            float speed = gpsLocation.getSpeed() * 3.6f;   //转成 公里/时


            Log.v("lhh", "maxSpeed" + maxSpeed + "当前速度:" + speed+"|time:"+location.getTime()+"|String:"+new TimeTool().getGPSTime(location.getTime()));
            ++zhengchang;


            if (speed >= maxSpeed) {

                ++chaosu;
                Log.v("lhh", "GpsHelper chaosu" + chaosu);
                if (chaosu >= 5) {


                    logger.info("---- Report OverSpeed Alarm ---");

                    // 生成 超速报警 消息
                    CAAlarmReportMessage alarmMessage = new CAAlarmReportMessage(ObdMessageID.OVER_SPEED_ALARM, true);

                    //alarmMessage.setGpsTime(System.currentTimeMillis() - 18000);
                    alarmMessage.setGpsTime(gpsLocation.getTime());


                    alarmMessage.setFixed(gpsFixed);
                    alarmMessage.setLat(gpsLocation.getLatitude());
                    alarmMessage.setLng(gpsLocation.getLongitude());
                    alarmMessage.setAlt(gpsLocation.getAltitude());
                    alarmMessage.setGpsSpeed(speed);
                    alarmMessage.setBearing(gpsLocation.getBearing());


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

                logger.info("---- Report GPS Data ---");

                // 生成 位置汇报 消息
                CALocationReportMessage lrpMessage = new CALocationReportMessage(ObdMessageID.OBD_REPORT, true);

                lrpMessage.setGpsTime(System.currentTimeMillis() - 18000);

                L.d("System.currentTimeMillis() :"+(System.currentTimeMillis()-18000));

                lrpMessage.setFixed(gpsFixed);
                lrpMessage.setLat(gpsLocation.getLatitude());
                lrpMessage.setLng(gpsLocation.getLongitude());
                lrpMessage.setAlt(gpsLocation.getAltitude());
                lrpMessage.setGpsSpeed(speed);
                lrpMessage.setBearing(gpsLocation.getBearing());
                lrpMessage.setTimeStamp(gpsLocation.getTime());

                String time = new TimeTool().getGPSTime(gpsLocation.getTime());
                L.d("当前时间:"+time);

//                Toast.makeText(context, "当前坐标" + gpsLocation.getLatitude() + "|" + gpsLocation.getLongitude(), Toast.LENGTH_LONG).show();

                lrpMessage.setEngineStatus(EngineStatus.ACC_ON.value());


                data.putSerializable("location", lrpMessage);

                zhengchang = 0;

                Message message2 = Message.obtain();
                message2.setData(data);
                message2.what = CommonMessage.MSG_LOCATION_REPORT;

                commHandler.sendMessage(message2);


            }
//            Toast.makeText(context, "当前坐标" + gpsLocation.getLatitude() + "|" + gpsLocation.getLongitude(), Toast.LENGTH_LONG).show();



            //TODO
            // 补充音乐播放、发送广播给Activity等操作
//            Intent intents = new Intent();
//            intents.putExtra("lat", gpsLocation == null ? "" : location.getLatitude() + "");
//            intents.putExtra("lon", gpsLocation == null ? "" : location.getLongitude() + "");
//            intents.putExtra("速度", speed);
//            intents.putExtra("海拔", gpsLocation.getAltitude() + "");
//            intents.putExtra("方向", gpsLocation.getBearing() + "");
//            intents.putExtra("超速", chaosu + "");
//            intents.putExtra("chaosu", Num + "");
//            intents.putExtra("超速标识符", isOverSpeed);
//            intents.setAction("Gps");
//            context.sendBroadcast(intents);


            Message message = new Message();
            message.what = Config.GPS;
            message.obj = new ShowGpsBean(location.getLatitude(),location.getLongitude()
            ,speed,isOverSpeed,location.getTime());
            mainHandler.sendMessage(message);
        }
    };

    protected void initGpsManager() {
        logger.info("init GpsManager");

        locationMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);


        //Settings.Secure.setLocationProviderEnabled(context.getContentResolver(),
        //        LocationManager.GPS_PROVIDER, true);

        gpsProvider = locationMgr.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        gpsLocation = locationMgr.getLastKnownLocation(gpsProvider);
    }


    protected static boolean gpsStarted = false;


    /**
     * 开启GPS监听器
     */
    protected void startGpsListening() {

        if (!gpsStarted) {

            logger.info("start GPS listening");


            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationMgr.requestLocationUpdates(gpsProvider, 2000, 0, locationListener);

            //locationMgr.addNmeaListener(nmeaListener);


            gpsStarted = true;


        }
    }


    /**
     * 关闭GPS监听器
     */
    protected void stopGpsListening() {
        if (gpsStarted) {

            isWorking = false;

            chaosu = 0;

            Num = 0;

            GpsLoactionMaxTime = 0;

            gpsStatusNum = -1;

            Distance = -100;//  don't stopgps
            Place = "";//地点

            logger.info("stop GPS listening");

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationMgr.removeUpdates(locationListener);
            //locationMgr.removeNmeaListener(nmeaListener);

            gpsStarted = false;



            //修改工作状态为 false
            SharedPreferences.Editor editd = context.getSharedPreferences("rlog", Activity.MODE_PRIVATE).edit();
            editd.putBoolean("isWorking", false);
            editd.putBoolean("activity", false);
            editd.commit();

        }

    }







}

