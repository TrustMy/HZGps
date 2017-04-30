package com.sy.hzgps.bean;

/**
 * Created by Trust on 2017/4/29.
 */

public class GpsBean {
    private double lat,lon;
    private long time;
    private int carStatus;
    public GpsBean(double lat, double lon, long time ,int carStatus) {
        this.lat = lat;
        this.lon = lon;
        this.time = time;
        this.carStatus = carStatus;
    }

    public int getCarStatus() {
        return carStatus;
    }

    public void setCarStatus(int carStatus) {
        this.carStatus = carStatus;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    @Override
    public String toString() {
        return "time:"+time+"|lat:"+lat+"|lon:"+lon+"|carStatus:"+carStatus;
    }
}
