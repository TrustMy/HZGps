package com.sy.hzgps.bean;

/**
 * Created by Trust on 2017/4/11.
 */
public class ShowGpsBean {
    private double lat,lon;
    private float speed;
    private boolean isOverSpeed;
    private long time;

    public ShowGpsBean(double lat, double lon, float speed, boolean isOverSpeed , long time) {
        this.lat = lat;
        this.lon = lon;
        this.speed = speed;
        this.time = time;
        this.isOverSpeed = isOverSpeed;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isOverSpeed() {
        return isOverSpeed;
    }

    public void setOverSpeed(boolean overSpeed) {
        isOverSpeed = overSpeed;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "坐标:"+getLat()+"|"+getLon()+"|speed:"+getSpeed();
    }
}
