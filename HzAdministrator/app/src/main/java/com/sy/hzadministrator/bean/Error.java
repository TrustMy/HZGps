package com.sy.hzadministrator.bean;

/**
 * Created by Trust on 2017/4/13.
 */
public class Error {


    /**
     * err : 参数类型错误
     * status : false
     */

    private String err;
    private boolean status;

    public void setErr(String err) {
        this.err = err;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getErr() {
        return err;
    }

    public boolean getStatus() {
        return status;
    }
}
