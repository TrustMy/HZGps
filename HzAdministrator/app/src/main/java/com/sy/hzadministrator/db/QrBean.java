package com.sy.hzadministrator.db;

/**
 * Created by Trust on 2017/4/12.
 */
public class QrBean {


    /**
     * order : 2017041217383100000001
     * time : 2017-04-12 17:38:31
     * termId : 1173
     * startName : 上哈
     * endName : 鄂尔多斯
     */

    private String order;
    private String time;
    private int termId;
    private String startName;
    private String endName;
    private long startTime,endTime,generatePictureTime;

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

    public void setOrder(String order) {
        this.order = order;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }

    public String getOrder() {
        return order;
    }

    public String getTime() {
        return time;
    }

    public int getTermId() {
        return termId;
    }

    public String getStartName() {
        return startName;
    }

    public String getEndName() {
        return endName;
    }
}
