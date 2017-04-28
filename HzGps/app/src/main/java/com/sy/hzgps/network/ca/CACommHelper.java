package com.sy.hzgps.network.ca;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;


import com.sy.hzgps.Config;
import com.sy.hzgps.tool.lh.L;
import com.sy.hzgps.tool.lh.T;
import com.sy.hzgps.tool.sy.Constants;
import com.sy.hzgps.MyContext;
import com.sy.hzgps.tool.sy.ServerType;
import com.sy.hzgps.message.CommonMessage;
import com.sy.hzgps.message.ObdMessage;
import com.sy.hzgps.message.ObdMessageID;
import com.sy.hzgps.network.CommHelper;
import com.sy.hzgps.protocol.SY808Message;
import com.sy.hzgps.protocol.SY808MessageHeader;
import com.sy.hzgps.protocol.SY_8001;
import com.sy.hzgps.protocol.SY_81AA;
import com.sy.hzgps.tool.sy.Tools;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by jiayang on 2016/7/17.
 */
public class CACommHelper extends CommHelper {

    private static Logger logger = LoggerFactory.getLogger(CACommHelper.class);


    private Context context = MyContext.getContext();

    private boolean needRegister = false;

    public void setNeedRegister(boolean v) {
        this.needRegister = v;
    }

    private boolean workingStatus = false;

    public void setWorkingStatus(boolean v) {
        workingStatus = v;
    }

    public boolean getWorkingStatus() {
        return workingStatus;
    }

    private boolean registered = false;
    private String authCode = null;

    public String getAuthCode() {
        return authCode;
    }


    public void setRegister(boolean v) {
        this.registered = v;
    }

    public static Handler loginHandler;
    @Override
    protected void initCommParams() {
        super.initCommParams();

        SharedPreferences prefs = context.getSharedPreferences("CommParams", Context.MODE_PRIVATE);

        authCode = prefs.getString("authCode", null);

        registered = prefs.getBoolean("registered", false);




    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {

    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {


        logger.info("Session Opened");
        L.d("Session Opened");

        isWaitForResponse = false;
        L.d("registered:"+registered+"|commClosed:"+commClosed+"|needRegister:"+needRegister);
        if ( registered ) {

            logger.info("Authenticate after connection");
            L.d("Authenticate after connection");
            synchronized (commClosed ) {
                commClosed = false;
            }
            // 发送鉴权消息
            handler.sendEmptyMessage(CommonMessage.MSG_TERMINAL_AUTHENTICATE);
        } else {
            if ( needRegister ) {
                logger.info("Register after connection");
                L.d("Register after connection");
                synchronized (commClosed ) {
                    commClosed = false;
                }
                // 发送注册消息
                handler.sendEmptyMessage(CommonMessage.MSG_TERMINAL_REGISTER);
            }
        }

    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {

        logger.info("Session closed");
        L.d("Session closed");
        handler.sendEmptyMessageDelayed(CommonMessage.MSG_SERVER_DISCONNECTED, Constants.SECOND*5);


    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

        if ( status == IdleStatus.WRITER_IDLE ) {

            //发送心跳
            //putTask(new ObdMessage(ObdMessageID.HEARTBEAT, false), false);

            logger.info("no task to send, close session");
            L.d("no task to send, close session");
            disConnectFromServer();

        } else if ( status == IdleStatus.READER_IDLE ) {

            logger.info("long time no message from server, close session");
            L.d("long time no message from server, close session");
            //session.closeNow();
            disConnectFromServer();
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {

        logger.info("ExceptionCaught");
        L.d("ExceptionCaught cause:"+cause+"|session:"+session);
        //session.closeNow();

        disConnectFromServer();

    }

    @Override
    public void inputClosed(IoSession session) throws Exception {

        logger.info("inputClose");
        L.d("inputClose");
        //session.closeNow();

        disConnectFromServer();

    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {

        taskTimeOutTimes = 0;

        SY808Message srvMessage = (SY808Message) message;

        SY808MessageHeader srvMessageHeader = srvMessage.getMessageHeader();

        switch(srvMessageHeader.getMessageType()) {
            case (short) 0x81AA:
                //成功 取消
                process_81AA(srvMessage, session);
                break;

            case (short) 0x8001:
                process_8001(srvMessage, session);
                break;

            default:
                logger.info("Unknown Protocol 0x%04x", srvMessageHeader.getMessageType());
                L.d("Unknown Protocol 0x%04x"+ srvMessageHeader.getMessageType());
                break;
        }

    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {

    }

    @Override
    protected void initMina() {
        // 初始化Mina组件
        try {
            connector = new NioSocketConnector(); //new NioDatagramConnector();

            // 设置连接超时
            connector.setConnectTimeoutMillis(Constants.SECOND*30);

            // 设置读写超时
            connector.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE, 120);
            connector.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE, 140);

            // 添加消息处理实体
            connector.setHandler(this);

            // 添加内存池过滤器
            //connector.getFilterChain().addLast("threadPool", new ExecutorFilter(threadPool));

            // 添加日志过滤器
            LoggingFilter loggingFilter = new LoggingFilter();
            loggingFilter.setSessionClosedLogLevel(LogLevel.NONE);
            loggingFilter.setSessionCreatedLogLevel(LogLevel.NONE);
            loggingFilter.setSessionOpenedLogLevel(LogLevel.NONE);
            loggingFilter.setMessageSentLogLevel(LogLevel.NONE);

            loggingFilter.setMessageReceivedLogLevel(LogLevel.NONE);
            loggingFilter.setSessionIdleLogLevel(LogLevel.NONE);

            connector.getFilterChain().addLast("logger", loggingFilter);

            // 添加编解码过滤器
            connector.getFilterChain().addLast("codec",
                    new ProtocolCodecFilter(new CAProtocolCodecFactory(this)));

        } catch (Exception e) {
            String info = e.getLocalizedMessage();
            logger.error("message : " + info);
            L.d("message : " + info);
        }
    }



    @Override
    protected void init() {


        super.init();

    }

    @Override
    public void run() {
        Looper.prepare();

        init();

        Looper.loop();
    }

    private int connectTimes = 1;
    public void connectToServer(final ServerType serverType) {

        String url;
        int port;

        SharedPreferences prefs = context.getSharedPreferences("CommParams", Context.MODE_PRIVATE);

        switch (serverType) {
            case REG_SERVER:
                url = regServerUrl;
                port = regServerPort;
                L.d("Connect to Register Server ... ... \" + regServerUrl + \":\" + regServerPort");
                logger.info("Connect to Register Server ... ... " + regServerUrl + ":" + regServerPort);
                break;

            case MAIN_SERVER:
                url = serverUrl;
                port = serverPort;
                logger.info("Connect to Main Server ... ... " + serverUrl + ":" + serverPort);
                L.d("Connect to Main Server ... ... " + serverUrl + ":" + serverPort);
                break;

            case BACKUP_SERVER:
                url = server2Url;
                port = server2Port;
                L.d("Connect to Backup Server ... ... " + server2Url + ":" + server2Port);
                logger.info("Connect to Backup Server ... ... " + server2Url + ":" + server2Port);
                break;

            default:
                url = serverUrl;
                port = serverPort;
                L.d("default to connect to Main Server ... ... " + serverUrl + ":" + serverPort);
                logger.info("default to connect to Main Server ... ... " + serverUrl + ":" + serverPort);
                break;

        }


        // 删除监听器
        if ( futureListener != null ) {
            connFuture.removeListener(futureListener);
            // 强制内存回收
            futureListener = null;
        }

        // 连接服务器
        connFuture = connector.connect(new InetSocketAddress(url, port));

        futureListener = new IoFutureListener() {
            public void operationComplete(IoFuture future) {
                ConnectFuture connFuture = (ConnectFuture) future;
                if (connFuture.isConnected()) {
                    session = future.getSession();
                    session.getConfig().setUseReadOperation(true);

                    connectTimes = 1;

                    //isConnected = true;
                    //connStatus = ConnectStatus.CONNECTED;
                    logger.info("Connection is successfully!");
                    L.d("Connection is successfully!");

                } else {
                    session = null;

                    try {
                        Thread.sleep(Constants.SECOND*10*connectTimes);

                        //logger.info("retry = " + retryCount);
                        if ( connectTimes < 8 ) {
                            connectTimes = connectTimes*2;
                        } else {

                            if ( serverType == ServerType.REG_SERVER ) {
                                // 通知前台
                                Intent intent = new Intent();

                                intent.setAction("register");

                                intent.putExtra("results", false);

                                context.sendBroadcast(intent);

                                return;
                            }

                        }
                    } catch (InterruptedException e) {
                        L.d("sleep exception!");
                        logger.error("sleep exception!");
                    }

                    if ( (workingStatus || serverType == ServerType.REG_SERVER ) && isNetConnected() ) {

                        Message msg = Message.obtain();

                        msg.what = CommonMessage.MSG_CONNECT_TO_SERVER;

                        if (serverType != ServerType.REG_SERVER) {
                            if (connectTimes < 8) {
                                msg.arg1 = ServerType.MAIN_SERVER.value();
                            } else {
                                msg.arg1 = ServerType.BACKUP_SERVER.value();
                            }
                        } else {
                            msg.arg1 = ServerType.REG_SERVER.value();
                        }

                        handler.sendMessage(msg);
                    }
                }
            }
        };


        if ( connFuture != null ) {
            connFuture.addListener(futureListener);
        }

    }

    public void serverDisconnected() {

        // 仅当数据通道连接正常，并且处于工作状态时，需要马上连接服务器
        if ( carrierReachable && workingStatus ) {
            if (registered) {
                connectToServer(ServerType.MAIN_SERVER);
            } else {
                connectToServer(ServerType.REG_SERVER);
            }
        }

    }

    private volatile Boolean commClosed = true;

    public boolean getCommClosed() {
        return commClosed;
    }

    public void disConnectFromServer() {

        synchronized (commClosed ) {
            if (commClosed) {
                return;
            }

            if (session != null) {

                logger.info("disconnect Mina");
                L.d("disconnect Mina");
                synchronized (isWaitForResponse) {
                    //取消服务器回复的检查
                    cancelResponseChecking();
                }

                saveAllToDB();
;
                connFuture.removeListener(futureListener);
                futureListener = null;

                //isConnected = false;
                //connStatus = ConnectStatus.DISCONNECTED;
                session.closeNow();
                //session.getCloseFuture().awaitUninterruptibly(1000);
                //connector.dispose();
                session = null;
                connFuture = null;


                commClosed = true;
            }
        }

    }


    private static int taskTimeOutTimes = 0;
    @Override
    protected void dealTaskTimeOut(short serialNo) {

        if ( taskTimeOutTimes > 10 ) {

            session.closeNow();
            context.sendBroadcast(new Intent().setAction("SessionClose"));
            return;
        }

        // 处理未收到服务器返回的定时器任务
        synchronized (taskQueue) {

            for (ObdMessage task : taskQueue) {

                if (task.getSerialNo() == serialNo) {

                    int sendTimes = task.getSendTimes();

                    // 重发未超过3次，再次重发
                    if (sendTimes < 3) {

                        logger.info("SN = " + serialNo + " sendTime = " + sendTimes );
                        L.d("SN = " + serialNo + " sendTime = " + sendTimes);
                    } else {

                        //logger.info("SN = " + serialNo + " sendTime = " + sendTimes + " --------------------------");

                        taskTimeOutTimes++;
                        // 保存到数据库
                        saveToDB(task);
                        taskQueue.remove(task);

                    }

                    break;
                } else {
                    //logger.info("111111111111111111111111111111111111111");
                }
            }

            isWaitForResponse = false;

            handler.sendEmptyMessage(CommonMessage.MSG_SEND_TASK);
        }
    }

    @Override
    public void dealDataTunnelConnected() {

        if ( workingStatus ) {
            // 已注册，连接通信服务器
            connectToServer(ServerType.MAIN_SERVER);

        }

    }

    @Override
    public void dealDataTunnelDisconnected() {

        disConnectFromServer();

    }

    @Override
    public void registerToServer() {

        //handler.sendEmptyMessage(CommonMessage.MSG_CONNECT_TO_SERVER);
        putTask(new ObdMessage(ObdMessageID.REGISTER, false), true);
    }

    @Override
    public void authenticateToServer() {
        putTask(new ObdMessage(ObdMessageID.AUTHENTICATE, false), true);
    }


    private void process_81AA(SY808Message message, IoSession session) {

        SY_81AA body = (SY_81AA) message.getMessageBody();

        Intent intent = new Intent();

        intent.setAction("register");



        if ( body != null ) {

            short messageSerialNo = body.getRegisterResponseMessageSerialNo();

            // 删除队列中的该消息
            removeTask(messageSerialNo);

            int replyResult = body.getRegisterResponseResult();

            if (replyResult == 0 || replyResult == 1 ) {

                logger.info(String.format("Register Reply, SN = 0x%04x", messageSerialNo));
                L.d(String.format("Register Reply, SN = 0x%04x", messageSerialNo));

                //向注册页面发广播

                intent.putExtra("results", true);

                context.sendBroadcast(intent);

                L.d("loging success");



                authCode = body.getAuthCode();
                serverUrl = body.getCommHost();
                serverPort = body.getCommPort();
                server2Url = body.getComm2Host();
                server2Port = body.getComm2Port();

                SharedPreferences.Editor editor = context.getSharedPreferences("CommParams", Context.MODE_PRIVATE).edit();

                registered = true;
                editor.putBoolean("registered", registered);
                editor.putString("authCode", authCode);
                editor.putString("serverUrl", serverUrl);
                editor.putInt("serverPort", serverPort);
                editor.putString("server2Url", server2Url);
                editor.putInt("server2Port", server2Port);

                editor.commit();


                needRegister = false;

                Message logMessage = new Message();
                logMessage.what = Config.LOGING;
                logMessage.arg1 = Config.LOGING_SUCCESS;
                loginHandler.sendMessage(logMessage);

                // 断开与RegServer的连接
                //
                disConnectFromServer();
                //session.closeNow();

                //Message msg = Message.obtain();
                //msg.what = CommonMessage.MSG_CONNECT_TO_SERVER;
                //msg.arg1 = ServerType.MAIN_SERVER.value();
                //handler.sendMessageDelayed(msg, Constants.SECOND);



            } else {
                String info = String.format("Register failure, reply : " +
                        Tools.ToHexFormatString(message.writeToBytes()));
                logger.info(info);




                Message logMessage = new Message();
                logMessage.what = Config.LOGING;
                logMessage.arg1 = Config.LOGING_ERROR;
                logMessage.obj = "登录失败!请确定用户名输入正确.";
                loginHandler.sendMessage(logMessage);


            }
        }

        synchronized (isWaitForResponse) {
            isWaitForResponse = false;
        }

        // 注册未成功,再次注册
        //if ( !registered ) {
        //    handler.sendEmptyMessageDelayed(CommonMessage.MSG_TERMINAL_REGISTER, Constants.SECOND*5);
        //}
    }

    private void process_8001(SY808Message message, IoSession session) {

        SY_8001 body = (SY_8001) message.getMessageBody();

        if ( body != null ) {
            short messageSerialNo = body.getResponseMessageSerialNo();

            // 删除队列中的该消息
            if ( !removeTask(messageSerialNo) ) {
                // 删除失败，说明该消息已经存入数据，需要从数据库中删除
                //dbManager.delete(messageSerialNo);
            }

            short messageType = body.getResponseMessageId();

            int replyResult = body.getResponseResult();

            if ( replyResult == 1 ) {
                logger.info("Invalid device reply");
                Message message1 = Message.obtain();
                message1.what = Config.LOGING;
                message1.arg1 = Config.LOGING_ERROR;
                message1.obj = "非注册司机,请尽快注册.否则无法产生数据!";
                loginHandler.sendMessage(message1);

            }

            // 针对Type进行相应处理
            switch( messageType ) {

                // 鉴权回复结果
                case (short) 0x0102:

                    if (replyResult == 0) {

                        logger.info(String.format("Authentication Reply, SN = 0x%04x", messageSerialNo));

                        L.d(String.format("Authentication Reply, SN = 0x%04x", messageSerialNo));
                    } else {
                        logger.info("---0102 replyResult = " + replyResult );
                        L.d("---0102 replyResult = " + replyResult);
                    }


                    break;

                case (short) 0x0200:


                    // 0 表示位置汇报成功，4表示报警汇报成功
                    if (replyResult == 0 || replyResult == 4) {
                        L.d("位置汇报成功");
                        logger.info(String.format("0200 Reply, SN = 0x%04x", messageSerialNo));
                        L.d(String.format("0200 Reply, SN = 0x%04x", messageSerialNo));

                    } else {
                        L.d("位置汇报失败");
                        logger.info("---0200 replyResult = " + replyResult );
                        L.d("---0200 replyResult = " + replyResult);
                    }

                    break;

                case (short) 0x0900:

                    if (replyResult == 0 ) {

                        logger.info(String.format("0900 Reply, SN = 0x%04x", messageSerialNo));
                        L.d(String.format("0900 Reply, SN = 0x%04x", messageSerialNo));

                    } else {
                        logger.info("---0900 replyResult = " + replyResult );
                        L.d("---0900 replyResult = " + replyResult );
                    }

                    break;


                default:

                    logger.info(String.format("0001 Reply, SN = 0x%04x", messageSerialNo));
                    L.d(String.format("0001 Reply, SN = 0x%04x", messageSerialNo));
                    break;
            }


        } else {
            logger.info("Parse SY8001 ( Common Reply ) failure!");
            L.d("Parse SY8001 ( Common Reply ) failure!");
        }

        synchronized (isWaitForResponse) {
            isWaitForResponse = false;
        }
        // 取队列中下个任务进行发送
        handler.sendEmptyMessageDelayed(CommonMessage.MSG_SEND_TASK, Constants.SECOND);
    }

}
