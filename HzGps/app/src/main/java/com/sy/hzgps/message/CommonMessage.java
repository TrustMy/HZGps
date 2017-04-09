package com.sy.hzgps.message;

/**
 * Created by jiayang on 2016/3/17.
 */
public class CommonMessage {


    // 系统相关消息  0x01XX
    public static final int MSG_DATA_TUNNEL_CONNECTED = 0x0104;
    public static final int MSG_DATA_TUNNEL_DISCONNECTED = 0x0105;


    // 通信相关消息  0x02XX
    public static final int MSG_CONNECT_TO_SERVER = 0x0201;
    public static final int MSG_SERVER_CONNECTED = 0x02F1;

    public static final int MSG_DISCONNECT_FROM_SERVER = 0x0202;
    public static final int MSG_SERVER_DISCONNECTED = 0x02F2;

    public static final int MSG_SEND_TASK = 0x0203;

    public static final int MSG_TERMINAL_REGISTER = 0x0204;
    public static final int MSG_TERMINAL_AUTHENTICATE = 0x0205;
    public static final int MSG_TERMINAL_HEARTBEAT = 0x0206;

    public static final int MSG_DEAL_TASK_TIMEOUT = 0x0210;




    // 车辆运行期间的各种消息  0x03XX
    public static final int MSG_LOCATION_REPORT = 0x0303;   // 位置汇报


    // GPS相关消息
    public static final int MSG_START_GPS_LISTENING = 0x0401;
    public static final int MSG_STOP_GPS_LISTENING = 0x0402;
    
}
