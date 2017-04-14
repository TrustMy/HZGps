package com.sy.hzadministrator.bean;

/**
 * Created by Trust on 2017/4/13.
 */
public class RequestDataBean {


    /**
     * confirmStatus : false
     * status : true
     */

    private boolean confirmStatus;
    private boolean status;

    public void setConfirmStatus(boolean confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getConfirmStatus() {
        return confirmStatus;
    }

    public boolean getStatus() {
        return status;
    }
}
