package com.sy.hzgps.tool.sy;

import android.content.Context;
import android.os.PowerManager;
import android.telephony.TelephonyManager;


import com.sy.hzgps.MyContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jiayang on 2016/7/12.
 */
public class SysTools {

    private static Logger logger = LoggerFactory.getLogger(SysTools.class);

    private static Context context = MyContext.getContext();


    public static String getIMSINumber() {

        TelephonyManager teleManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return teleManager.getSubscriberId();
    }

    public static int getSimCardStatus() {
        TelephonyManager teleManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        return teleManager.getSimState();
    }

    public static String getTerminalId() {

        String str = getIMSINumber();

        if ( str != null ) {
            return str.substring(3);
        } else {
            return null;
        }
    }

    private static PowerManager.WakeLock wakeLock = null;

    public static void acquireWakeLock(Context context) {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SYOBD");
        }


        if ( wakeLock != null && !wakeLock.isHeld() ) {
            logger.info("Acquiring wake lock");
            wakeLock.acquire();
        } else {
            logger.info("Failed to acquiring wake lock");
        }
    }


    public static void releaseWakeLock() {
        if (wakeLock !=null && wakeLock.isHeld()) {
            logger.info("Release wake lock");
            wakeLock.release();
        } else {
            logger.info("Failed to release wake lock");
        }
    }





}
