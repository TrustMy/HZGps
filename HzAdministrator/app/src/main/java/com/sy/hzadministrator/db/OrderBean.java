package com.sy.hzadministrator.db;

import android.graphics.Bitmap;

/**
 * Created by Trust on 2017/4/12.
 */
public class OrderBean {
    private String order,startName,endName,time;
    private Bitmap qR;
    private String workTime;
    private int termId,status;
    private long startTime,endTime,generatePictureTime;
    public OrderBean(String order, String startName, String endName, String time,
                     Bitmap qR, int termId,String workTime,long startTime,long endTime,
                     long generatePictureTime,int status) {
        this.order = order;
        this.status = status;
        this.startName = startName;
        this.endName = endName;
        this.time = time;
        this.workTime = workTime;
        this.qR = qR;
        this.termId = termId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.generatePictureTime = generatePictureTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getGeneratePictureTime() {
        return generatePictureTime;
    }

    public void setGeneratePictureTime(long generatePictureTime) {
        this.generatePictureTime = generatePictureTime;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public String getEndName() {
        return endName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Bitmap getqR() {
        return qR;
    }

    public void setqR(Bitmap qR) {
        this.qR = qR;
    }

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }
}
