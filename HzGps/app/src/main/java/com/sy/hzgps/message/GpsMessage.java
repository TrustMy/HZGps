package com.sy.hzgps.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jiayang on 2016/7/14.
 */
public class GpsMessage extends ObdMessage {

    /**
     * GPS时间
     */
    private long gpsTime = 0L;

    public void setGpsTime(long gpsTime) {
        this.gpsTime = gpsTime;
    }

    public long getGpsTime() {
        return gpsTime;
    }

    /**
     * 纬度
     */
    private double lat = 0.0f;

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLat() {
        return lat;
    }

    /**
     * 经度
     */
    private double lng = 0.0f;

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLng() {
        return lng;
    }

    /**
     * 海拔
     */
    private double alt = 0.0f;

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public double getAlt() {
        return alt;
    }

    /**
     * 速度
     */
    private float gpsSpeed = 0.0f;

    public void setGpsSpeed(float gpsSpeed) {
        this.gpsSpeed = gpsSpeed;
    }

    public float getGpsSpeed() {
        return gpsSpeed;
    }

    /**
     * 方向
     */
    private float bearing = 0.0f;

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public float getBearing() {
        return bearing;
    }

    /**
     * 是否定位
     */
    private boolean fixed = false;

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public boolean getFixed() {
        return fixed;
    }

    /**
     * 引擎状态
     */
    private int engineStatus = 0;

    public void setEngineStatus(int engineStatus) {
        this.engineStatus = engineStatus;
    }

    public int getEngineStatus() {
        return engineStatus;
    }


    public GpsMessage(ObdMessageID messageId, boolean needSave) {
        super(messageId, needSave);
    }



    @Override
    public String toString() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(gpsTime);

        String sDate = sdf.format(date);
        return String.format(Locale.CHINESE, "%s, 定位 = %b, 引擎 = %b, 纬度 = %f, 经度 = %f, 海拔 = %f, 速度 = %f, 方向 = %f",
                sDate, fixed, engineStatus, lat, lng, alt, gpsSpeed, bearing);
    }
}
