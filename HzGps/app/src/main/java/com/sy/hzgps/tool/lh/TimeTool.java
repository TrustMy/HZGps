package com.sy.hzgps.tool.lh;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Trust on 2017/4/10.
 */
public class TimeTool {
    public static String  getSystemTime()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTime = new Date(System.currentTimeMillis());//获取当前时间
        String systemTime = formatter.format(dateTime);
        return systemTime;
    }

    public static long getSystemTimeDate(){
        return    System.currentTimeMillis();
    }

}
