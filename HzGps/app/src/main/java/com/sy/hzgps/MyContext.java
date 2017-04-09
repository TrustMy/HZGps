package com.sy.hzgps;

import android.app.Application;
import android.content.Context;

/**
 * Created by Trust on 2017/3/28.
 */
public class MyContext extends Application {
    private static Context context = null;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext()
    {
        return context;
    }
}
