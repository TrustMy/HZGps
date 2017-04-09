package com.sy.hzgps.bean;

import android.graphics.Bitmap;

/**
 * Created by Trust on 2017/4/8.
 */

public class QRcodeBean {
    private Bitmap bitmap;
    private String time;

    public QRcodeBean(Bitmap bitmap, String time) {
        this.bitmap = bitmap;
        this.time = time;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
