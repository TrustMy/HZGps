package com.sy.hzgps;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

/**
 * Created by Trust on 2017/4/24.
 */
public class ApkConfig {

    public static long Time ;

    public static int PhoneCode = 10;

    public static String flieName = "karl";

    public static String fliePath = Environment.getExternalStorageDirectory()+"/com.coder/karl";

    public static Bitmap PhotoBitMap;

    public static String PhotoBitMapString;

    public static long startTime,endTime,generatePictureTime;

    public static double updateLat,updateLon;

    public static long twoMoth = 5270400000L;

    public static Uri imageUri;


}
