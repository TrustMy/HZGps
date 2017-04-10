package com.sy.hzgps.tool.lh;

import android.util.Log;

/**
 * Created by Trust on 2017/3/27.
 */
public class L {
    private static final String TAG_D = "Lhh_DeBug d";
    private static final String TAG_E = "Lhh_DeBug err";
    private static boolean isShow = true;
    public static void d(String msg)
    {
        if(isShow && msg != null)
        {
            Log.d(TAG_D, msg);
        }else
        {
            L.err("d : msg == null");
        }
    }
    public static void err(String msg)
    {
        if(isShow)
        {
            Log.e(TAG_E, msg);
        }
    }

}
