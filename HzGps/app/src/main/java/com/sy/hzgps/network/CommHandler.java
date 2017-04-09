package com.sy.hzgps.network;

import android.os.Handler;
import android.os.Message;


import com.sy.hzgps.ServerType;
import com.sy.hzgps.message.CommonMessage;
import com.sy.hzgps.message.GpsMessage;
import com.sy.hzgps.message.ObdMessage;
import com.sy.hzgps.message.ObdMessageID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;

/**
 * Created by jiayang on 2016/4/6.
 */
public class CommHandler extends Handler {

    private static Logger logger = LoggerFactory.getLogger(CommHandler.class);


    private final WeakReference<CommHelper> thread;

    public CommHandler(CommHelper t) {
        thread = new WeakReference<CommHelper>(t);
    }

    @Override
    public void handleMessage(Message msg) {

        CommHelper t = thread.get();
        if (t != null) {

            switch (msg.what) {

                case CommonMessage.MSG_SEND_TASK:
                    t.sendTask();
                    break;

                case CommonMessage.MSG_DEAL_TASK_TIMEOUT:
                    t.dealTaskTimeOut(msg.getData().getShort("serialNo"));
                    break;

                case CommonMessage.MSG_DATA_TUNNEL_CONNECTED:
                    t.dealDataTunnelConnected();
                    break;

                case CommonMessage.MSG_DATA_TUNNEL_DISCONNECTED:
                    t.dealDataTunnelDisconnected();
                    break;

                case CommonMessage.MSG_CONNECT_TO_SERVER:
                    ServerType type = ServerType.valueOf(msg.arg1);
                    t.connectToServer(type);
                    break;

                case CommonMessage.MSG_SERVER_CONNECTED:
                    break;

                case CommonMessage.MSG_SERVER_DISCONNECTED:
                    t.serverDisconnected();
                    break;

                case CommonMessage.MSG_DISCONNECT_FROM_SERVER:
                    t.disConnectFromServer();
                    break;

                case CommonMessage.MSG_TERMINAL_REGISTER:
                    //t.putTask(new ObdMessage(ObdMessageID.REGISTER, false), true);

                    t.registerToServer();
                    break;

                case CommonMessage.MSG_TERMINAL_AUTHENTICATE:
                    //t.putTask(new ObdMessage(ObdMessageID.AUTHENTICATE, false), true);
                    t.authenticateToServer();
                    break;

                case CommonMessage.MSG_TERMINAL_HEARTBEAT:
                    t.putTask(new ObdMessage(ObdMessageID.HEARTBEAT, false), false);
                    break;

                case CommonMessage.MSG_LOCATION_REPORT:
                    t.putTask((GpsMessage) msg.getData().get("location"), false);
                    break;

                default:
                    break;

            }


            super.handleMessage(msg);
        }
    }
}
