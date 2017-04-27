package com.sy.hzgps.gps;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.sy.hzgps.message.CommonMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;

/**
 * Created by jiayang on 2016/4/6.
 */
public class GpsHandler extends Handler {

    private static Logger logger = LoggerFactory.getLogger(GpsHandler.class);


    private final WeakReference<GapGpsHelper> thread;
    private GapGpsHelper gaoToGps ;
    public GpsHandler(GapGpsHelper t) {
//        thread = new WeakReference<GpsHelper>(t);
//        thread = new WeakReference<GapGpsHelper>((GapGpsHelper) t);
        thread = new WeakReference<GapGpsHelper>(t);
    }

    @Override
    public void handleMessage(Message msg) {

        GapGpsHelper t = thread.get();
        if (t != null) {

            switch (msg.what) {

                case CommonMessage.MSG_START_GPS_LISTENING:
                    t.startGps();
                    break;

                case CommonMessage.MSG_STOP_GPS_LISTENING:
                    t.stopGps();
                    break;


                default:
                    break;

            }


            super.handleMessage(msg);
        }
    }
}
