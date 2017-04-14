package com.sy.hzadministrator.bean;

/**
 * Created by Trust on 2017/4/13.
 */
public class LogErrBean {

    /**
     * status : false
     * reason : 该帐号不存在
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
