package com.sy.hzgps.bean;

/**
 * Created by Trust on 2017/4/13.
 */
public class Error {

    /**
     * status : false
     * err : 错误原因
     */

    private boolean status;
    private String err;

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public boolean getStatus() {
        return status;
    }

    public String getErr() {
        return err;
    }
}
