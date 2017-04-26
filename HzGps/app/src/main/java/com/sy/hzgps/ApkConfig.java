package com.sy.hzgps;

import android.graphics.Bitmap;
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

    public static long startTime,endTime,generatePictureTime;
}
