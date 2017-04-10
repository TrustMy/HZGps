package com.sy.hzgps.network;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;


import com.sy.hzgps.tool.sy.Constants;
import com.sy.hzgps.MyContext;
import com.sy.hzgps.tool.sy.ServerType;
import com.sy.hzgps.database.DBManager;
import com.sy.hzgps.message.CommonMessage;
import com.sy.hzgps.message.ObdMessage;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

/**
 * Created by jiayang on 2016/7/15.
 */
public abstract class CommHelper extends IoHandlerAdapter implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(CommHelper.class);

    private static Context context = MyContext.getContext();

    private DBManager dbManager = null;

    protected static LinkedList<ObdMessage> taskQueue = new LinkedList<ObdMessage>();


    protected String license = "";

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicense() {
        return license;
    }

    protected String terminalId = "";

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    protected String simNumber = "";

    public String getSimNumber() {
        return simNumber;
    }

    protected String serverUrl = "";
    protected int serverPort = 0;

    protected String server2Url = "";
    protected int server2Port = 0;

    protected String regServerUrl = "";
    protected int regServerPort = 0;


    protected short serialNo = 0x0000;

    protected static volatile Boolean isWaitForResponse = false;
    private static Short currentTaskSerialNo = null;

    protected IoConnector connector = null;
    protected ConnectFuture connFuture = null;
    protected volatile IoSession session = null;
    protected IoFutureListener futureListener = null;

    protected static Handler handler = null;

    public Handler getHandler() {
        return handler;
    }

    private Handler gpsHandler = null;

    public void setGpsHandler(Handler handler) {
        this.gpsHandler = handler;
    }

    protected Handler processHandler = null;

    public void setProcessHandler(Handler processHandler) {
        this.processHandler = processHandler;
    }

    protected AlarmManager alarmManager;

    protected PendingIntent checkResponsePendingIntent = null;


    private static final String CHECK_RESPONSE_ACTION = "com.shengyu.cagps.check_response_task_action";


    /*
    protected PendingIntent ledOffPendingIntent = null;
    private static final String LED_OFF_ACTION = "com.shengyu.syobd.led_off_task_action";
    public static class LedOffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(LED_OFF_ACTION)) {

                SysTools.stopLed();

            }
        }
    }*/

    public static class CheckResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CHECK_RESPONSE_ACTION)) {

                Message msg = Message.obtain();

                msg.what = CommonMessage.MSG_DEAL_TASK_TIMEOUT;

                Bundle bundle = new Bundle();
                bundle.putShort("serialNo", currentTaskSerialNo);

                msg.setData(bundle);

                handler.sendMessage(msg);


            }
        }
    }


    @Override
    public void sessionCreated(IoSession session) throws Exception {

    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {

    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {

    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {

    }

    @Override
    public void inputClosed(IoSession session) throws Exception {

    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {

    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {

    }


    public void putTask(ObdMessage message, boolean topPriority) {

        if ( message != null ) {

            /*// TEST Code
            if ( message.getMessageId() == ObdMessageID.OBD_REPORT) {

                message.setSerialNo((short)0x10);
                saveToDB(message);

                handler.sendEmptyMessage(CommonMessage.MSG_SEND_TASK);
                return;
            }*/

            synchronized (taskQueue) {

                if (topPriority) {
                    //logger.info("Put to queue header");
                    taskQueue.addFirst(message);
                } else {
                    taskQueue.offer(message);
                }
            }

            handler.sendEmptyMessage(CommonMessage.MSG_SEND_TASK);
        }
    }

    public ObdMessage fetchMessage() {

        synchronized (taskQueue ) {
            ObdMessage message = null;
            if (taskQueue.isEmpty()) {

                // 从数据库中读取历史任务
                message = getFromDB();

                if (message != null) {
                    logger.info("Fetched a task from DB");
                    taskQueue.offer(message);
                }
            }

            message = taskQueue.peek();

            if (message != null) {

                if (message.getSendTimes() == 0) {

                    // 仅对首次发送的任务添加序列号，重发的任务，沿用原来的序列号
                    message.setSerialNo(serialNo++);
                }
            }

            return message;
        }
    }

    public void sendTask() {

        synchronized (isWaitForResponse) {

            //logger.info("wait for response " + isWaitForResponse);


            // 当前没有发送任务等待服务器的回应时，才继续发送任务
            if ( !isWaitForResponse ) {

                ObdMessage message = fetchMessage();

                if ( message != null ) {

                    if ( session == null ) {
                        // 保存到数据库
                        saveToDB(message);

                        removeTask(message.getSerialNo());

                        return;
                    }


                    switch ( message.getMessageId() ) {
                        case REGISTER:
                            logger.info("---Register---");
                            break;

                        case AUTHENTICATE:
                            logger.info("---Authenticate---");
                            break;
                        case HEARTBEAT:
                            logger.info("---HeatBeat---");
                            break;
                        case OBD_REPORT:
                            logger.info("---LRP---");
                            break;
                        case OVER_SPEED_ALARM:
                            logger.info("---Over Speed Alarm---");
                            break;
                        case ENGINE_ON:
                            logger.info("---Engine On---");
                            break;
                        case ENGINE_OFF:
                            logger.info("---Engine Off---");
                            break;
                        case TRIP_REPORT:
                            logger.info("---Trip---");
                            break;
                        default:
                            logger.info("---UnKnown---");
                            break;
                    }

                    currentTaskSerialNo = message.getSerialNo();

                    // 发送次数++
                    message.incrSendTimes();


                    session.write(message);

                    // 定时器，检查服务器回应
                    alarmManager.set(AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + message.getSendTimes() * Constants.SECOND * 10,
                            checkResponsePendingIntent);

                    isWaitForResponse = true;
                }
            }

        }

    }



    protected abstract void initMina();

    protected abstract void dealDataTunnelConnected();

    protected abstract void dealDataTunnelDisconnected();

    protected abstract void connectToServer(ServerType type);

    protected abstract void serverDisconnected();

    protected abstract void disConnectFromServer();

    protected abstract void dealTaskTimeOut(short serialNo);

    protected abstract void registerToServer();

    protected abstract void authenticateToServer();

    public static boolean isNetConnected() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        return (info != null && info.isConnected());
    }

    protected void init() {

        logger.info("CommHelper init");

        // 注册数据通道监听器
        //registerPhoneStateListener();

        // 获取网络状态
        carrierReachable = isNetConnected();

        handler = new CommHandler(this);

        // 数据库助手
        dbManager = new DBManager(context);

        // 初始化通信参数
        initCommParams();


        // 初始化Mina通信框架
        initMina();

        // 初始化定时器任务
        initPeriodicTasks();


    }

    protected void initCommParams() {
        SharedPreferences prefs = context.getSharedPreferences("CommParams", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();

        regServerUrl = prefs.getString("regServerUrl", "carlinkcn.com");
        regServerPort = prefs.getInt("regServerPort", 8876);

        serverUrl = prefs.getString("serverUrl", "carlinkcn.com");
        serverPort = prefs.getInt("serverPort", 8876);

        server2Url = prefs.getString("server2Url", "carlinkcn.com");
        server2Port = prefs.getInt("server2Port", 8876);

        terminalId = prefs.getString("terminalId", "");


        editor.putString("regServerUrl", regServerUrl);
        editor.putInt("regServerPort", regServerPort);

        editor.putString("serverUrl", serverUrl);
        editor.putInt("serverPort", serverPort);

        editor.putString("server2Url", server2Url);
        editor.putInt("server2Port", server2Port);

        editor.commit();

    }

    protected void initPeriodicTasks() {

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // 初始化CheckTask定时器
        Intent checkIntent = new Intent(context, CheckResponseReceiver.class);
        checkIntent.setAction(CHECK_RESPONSE_ACTION);
        checkResponsePendingIntent = PendingIntent.getBroadcast(context, 0, checkIntent, 0);

    }

    @Override
    public void run() {
        Looper.prepare();

        init();

        Looper.loop();
    }

    public boolean removeTask(short serialNo) {


        // 取消检查服务器回复的定时器
        cancelResponseChecking();

        synchronized (taskQueue) {

            for (ObdMessage task : taskQueue) {

                //logger.info(String.format("SerialNo = 0x%04x", serialNo));
                if (task.getSerialNo() == serialNo) {
                    //logger.info(String.format("Remove 0x%04x", serialNo));
                    taskQueue.remove(task);
                    return true;
                }
            }

            return false;
        }
    }

    public void saveToDB(ObdMessage message) {
        if ( message != null ) {

            if ( message.getNeedSave() ) {


                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
                    objectOutputStream.writeObject(message);
                    objectOutputStream.flush();
                    byte data[] = arrayOutputStream.toByteArray();
                    objectOutputStream.close();
                    arrayOutputStream.close();

                    dbManager.openDB();
                    dbManager.add(message.getTimeStamp(), message.getSerialNo(), data);
                    dbManager.closeDB();


                    //logger.info(message.getTimeStamp() + " " + message.getSerialNo() + " " + Tools.ToHexFormatString(data));

                    logger.info(String.format("Save to DB, SN = 0x%04x, Type = 0x%04x",
                            message.getSerialNo(), message.getMessageId().value()));

                } catch (IOException e) {
                    logger.error(e.getLocalizedMessage());
                }
            }
        }
    }

    public void saveAllToDB() {
        synchronized (taskQueue ) {
            for( ObdMessage message : taskQueue ) {
                saveToDB(message);
            }

            taskQueue.clear();
        }
    }

    public ObdMessage getFromDB() {

        ObdMessage obdMessage;

        dbManager.openDB();
        DBManager.Record record = dbManager.getOneTask();

        if ( record != null ) {
            dbManager.delete(record.serialNo);
        }

        dbManager.closeDB();

        if ( record != null ) {
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(record.data);
            try {
                ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
                obdMessage = (ObdMessage) inputStream.readObject();
                inputStream.close();
                arrayInputStream.close();
                obdMessage.setSendTimes((short)0);

                return obdMessage;
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            }
        }

        return null;
    }

    public boolean taskQueueEmpty() {


        synchronized (taskQueue ) {

            return taskQueue.isEmpty();

        }

    }

    /**
     * 取消服务器回复的检查器
     */
    protected void cancelResponseChecking() {

        //logger.info("CommHelper: ---0200 cancelResponseChecking() 取消服务器回复的检查器" );
        synchronized (isWaitForResponse ) {

            //logger.info("Cancel Response Checking");
            alarmManager.cancel(checkResponsePendingIntent);
            isWaitForResponse = false;
        }

    }

    public static class NetStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            // TODO Auto-generated method stub
            //Toast.makeText(context, intent.getAction(), 1).show();
            if ( isNetConnected() ) {
                logger.info("Data connected");

                if ( !carrierReachable ) {

                    handler.sendEmptyMessage(CommonMessage.MSG_DATA_TUNNEL_CONNECTED);

                    carrierReachable = true;
                }

            } else {

                logger.info("Data disconnected");


                if ( carrierReachable ) {

                    handler.sendEmptyMessage(CommonMessage.MSG_DATA_TUNNEL_DISCONNECTED);

                }

                carrierReachable = false;

            }
        }

    }

    protected PhoneStateListener phoneStateListener = null;
    protected static boolean carrierReachable = false;
    /**
     * 注册数据通道监听器
     */
    protected void registerPhoneStateListener() {

        logger.info("start registerPhoneStateListener");

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);


        phoneStateListener = new PhoneStateListener() {

            public void onDataConnectionStateChanged(int state, int networkType) {

                switch (state) {
                    case TelephonyManager.DATA_CONNECTED:

                        logger.info("Data connected");

                        if ( !carrierReachable ) {

                            handler.sendEmptyMessage(CommonMessage.MSG_DATA_TUNNEL_CONNECTED);

                            carrierReachable = true;
                        }

                        break;

                    case TelephonyManager.DATA_DISCONNECTED:

                        logger.info("Data disconnected");


                        if ( carrierReachable ) {

                            handler.sendEmptyMessage(CommonMessage.MSG_DATA_TUNNEL_DISCONNECTED);

                        }

                        carrierReachable = false;

                        break;

                    default:
                        break;
                }
            }

        };

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);


    }

}
