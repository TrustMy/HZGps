package com.sy.hzadministrator.bean;

/**
 * Created by Trust on 2017/5/2.
 */
public class LogingErrBean {

    /**
     * status : false
     * reason : 密码错误
     */

    private String status;
    private String reason;

    public void setStatus(String status) {
        this.status = status;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }
}
