package com.sy.hzgps.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;


import com.sy.hzgps.ApkConfig;
import com.sy.hzgps.Config;
import com.sy.hzgps.MainActivity;
import com.sy.hzgps.gps.GapGpsHelper;
import com.sy.hzgps.tool.lh.L;
import com.sy.hzgps.tool.lh.TimeTool;
import com.sy.hzgps.tool.sy.EngineStatus;
import com.sy.hzgps.tool.sy.ServerType;
import com.sy.hzgps.gps.GpsHelper;
import com.sy.hzgps.message.CommonMessage;
import com.sy.hzgps.message.ObdMessageID;
import com.sy.hzgps.message.ca.CALocationReportMessage;
import com.sy.hzgps.network.CommHelper;
import com.sy.hzgps.network.ca.CACommHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyService extends Service {

    private static Logger logger = LoggerFactory.getLogger(MyService.class);

    protected static ExecutorService threadPool = Executors.newCachedThreadPool();

    private Binder myBinder = new MsgBinder();


    private CACommHelper commHelper;

    private Handler commHandler;

    private GapGpsHelper gapGpsHelper;
    private Handler gpsHandler;

    private boolean IsStopLoading = false;

    public static Handler mainHandler;

    private Handler timeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Config.TIME:
                    mainHandler.sendEmptyMessage(Config.TIME);
                    timeHandler.sendEmptyMessageDelayed(Config.TIME,60*1000);
                    break;
            }
        }
    };

    public MyService() {

    }

    @Override
    public void onCreate() {

        logger.info("\n\n\n\n\nCA Service Start ... ");


        //LoggerHelper.getInstance(this).start();


        commHelper = new CACommHelper();
        gapGpsHelper = new GapGpsHelper();

        threadPool.execute(commHelper);
        threadPool.execute(gapGpsHelper);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        commHandler = commHelper.getHandler();
        gpsHandler = gapGpsHelper.getHandler();

        commHelper.setGpsHandler(gpsHandler);
        gapGpsHelper.setCommHandler(commHandler);

    }





    @Override
    public IBinder onBind(Intent intent) {
        L.d("onBind");
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");

        //logger.info("Service onBind ...........................................");
        return myBinder;
    }

    @Override
    public void onDestroy() {
        L.d("onDestroy");
        //logger.info("Service Destroyed ..........................");

        gpsHandler.sendEmptyMessage(CommonMessage.MSG_STOP_GPS_LISTENING);

        //commHandler.sendEmptyMessage(CommonMessage.MSG_DISCONNECT_FROM_SERVER);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        L.d("onUnbind");

        //logger.info("Service Unbinded .............................");

        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        L.d("onStartCommand");

        return 0;

    }



    public class MsgBinder extends Binder {

        public MyService getService(){
            return MyService.this;
        }
    }

    public boolean isNetworkConnected() {
        return CommHelper.isNetConnected();
    }

    public void registerToServer(String license, String terminalId) {

        logger.info("register to server ...");
        L.d("register to server ...");
        commHelper.setTerminalId(terminalId);
        commHelper.setLicense(license);
        commHelper.setNeedRegister(true);

        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("CommParams", Context.MODE_PRIVATE).edit();

        editor.putString("terminalId", terminalId);
        editor.putString("license", license);

        editor.commit();

        //commHandler.sendEmptyMessage(CommonMessage.MSG_TERMINAL_REGISTER);
        Message message = Message.obtain();
        message.what = CommonMessage.MSG_CONNECT_TO_SERVER;
        message.arg1 = ServerType.REG_SERVER.value();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i =0;i<10;i++)
                {
//                    Log.d("MyService", "i:.." + i);
                    if(i == 9)
                    {
                        IsStopLoading =true;

                        sendBroadcast(new Intent().setAction("IsStopLoading").putExtra("IsStopLoadomg",IsStopLoading));
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        commHandler.sendMessage(message);

    }

    public void startWorking() {

        if ( commHelper.getWorkingStatus() ) {

            logger.info("Service is alreay in working status");

            return;
        }

        logger.info("start working ...");
        Log.d("lhh", "startWorking: ");
        // 通知GPS线程开始采集GPS数据
        gpsHandler.sendEmptyMessage(CommonMessage.MSG_START_GPS_LISTENING);


        commHelper.setWorkingStatus(true);

        if ( commHelper.isNetConnected() && commHelper.getCommClosed() ) {
            logger.info("start connecting to server");
            // 通知通信线程连接服务器
            Message message = Message.obtain();
            message.what = CommonMessage.MSG_CONNECT_TO_SERVER;
            message.arg1 = ServerType.MAIN_SERVER.value();

            commHandler.sendMessage(message);
        }

        // 发送开始工作的消息给服务器
        // 生成 位置汇报 消息
        CALocationReportMessage lrpMessage = new CALocationReportMessage(ObdMessageID.OBD_REPORT, true);
        //开始工作 gps 没信号 去系统时间
        if(ApkConfig.startTime == 0){
            ApkConfig.startTime = TimeTool.getSystemTimeDate();
        }
        lrpMessage.setGpsTime( ApkConfig.startTime);

        L.d("start working start Time :"+TimeTool.getSystemTimeDate());

        lrpMessage.setFixed(false);
        lrpMessage.setLat(0);
        lrpMessage.setLng(0);
        lrpMessage.setAlt(0);
        lrpMessage.setGpsSpeed(0);
        lrpMessage.setBearing(0);


        lrpMessage.setEngineStatus(EngineStatus.FIRE_ON.value());


        Bundle data = new Bundle();
        data.putSerializable("location", lrpMessage);



        Message message = Message.obtain();
        message.setData(data);
        message.what = CommonMessage.MSG_LOCATION_REPORT;

        commHandler.sendMessage(message);

        /**
         * 每1min发送一次 刷新 ui
         */
        timeHandler.sendEmptyMessageDelayed(Config.TIME,60*1000);

    }

    public void stopWorking() {
        logger.info("stop working ...");

        // 通知GPS线程停止采集GPS数据
        gpsHandler.sendEmptyMessage(CommonMessage.MSG_STOP_GPS_LISTENING);


        commHelper.setWorkingStatus(false);

        // 删除连接服务器的请求
        commHandler.removeMessages(CommonMessage.MSG_CONNECT_TO_SERVER);

        // 发送开始工作的消息给服务器
        // 生成 位置汇报 消息
        CALocationReportMessage lrpMessage = new CALocationReportMessage(ObdMessageID.OBD_REPORT, true);

        if(ApkConfig.Time == 0){
            //如果 gps 一直没信号 time 为 0  去当前系统时间
            ApkConfig.endTime = TimeTool.getSystemTimeDate();
        }
        lrpMessage.setGpsTime(ApkConfig.endTime);
        L.d("stopWorking ApkConfig.Time :"+ApkConfig.Time +"|ApkConfig.endTime:"+ApkConfig.endTime);

        lrpMessage.setFixed(false);
        lrpMessage.setLat(ApkConfig.updateLat);
        lrpMessage.setLng(ApkConfig.updateLon);
        lrpMessage.setAlt(0);
        lrpMessage.setGpsSpeed(0);
        lrpMessage.setBearing(0);
        L.d("end work gps time :"+lrpMessage.getGpsTime()+
                "|getTimeStamp"+lrpMessage.getTimeStamp());

        lrpMessage.setEngineStatus(EngineStatus.FIRE_OFF.value());


        Bundle data = new Bundle();
        data.putSerializable("location", lrpMessage);

        L.d("stopWorking endTime:"+lrpMessage.getGpsTime());

        Message message = Message.obtain();
        message.setData(data);
        message.what = CommonMessage.MSG_LOCATION_REPORT;

        commHandler.sendMessage(message);
        //commHandler.sendEmptyMessageDelayed(CommonMessage.MSG_DISCONNECT_FROM_SERVER, 5*Constants.SECOND);


        // 如果没有待发送的通信任务，直接断开连接
        //if ( commHelper.taskQueueEmpty()) {
        //    commHandler.sendEmptyMessage(CommonMessage.MSG_DISCONNECT_FROM_SERVER);
        //}

        timeHandler.removeMessages(Config.TIME);
    }

    public void setReadyToRegister() {
        commHelper.setRegister(false);
    }
}
