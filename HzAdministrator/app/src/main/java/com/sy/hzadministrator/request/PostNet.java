package com.sy.hzadministrator.request;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.sy.hzadministrator.Config;
import com.sy.hzadministrator.bean.Error;
import com.sy.hzadministrator.bean.LogErrBean;
import com.sy.hzadministrator.bean.RequestDataBean;


/**
 * Created by Trust on 2017/4/13.
 */
public class PostNet extends Handler {
    private Handler handler;
    private Gson gson;
    private Error error;
    public PostNet(Handler handler) {
        this.handler = handler;
        this.gson = new Gson();
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){

            case Config.ORDER:
                if(msg.arg1 == Config.RESULT_SUCCESS){
                    checkData((String)msg.obj,Config.ORDER);
                }else{
                    error = gson.fromJson(msg.obj.toString(),Error.class);
                    toHander(error.getErr(),Config.ORDER,Config.RESULT_ERROR);
                }
                break;
            case Config.LOGING:
                if(msg.arg1 == Config.RESULT_SUCCESS){
                    resultLogin((String)msg.obj,Config.LOGING);
                }else{
                    error = gson.fromJson((String)msg.obj,Error.class);
                    toHander(error.getErr(),Config.LOGING,Config.RESULT_ERROR);
                }
                break;
        }
    }

    private void resultLogin(String obj, int type) {
        RequestDataBean bean = gson.fromJson(obj,RequestDataBean.class);
        Error error = gson.fromJson(obj,Error.class);
        if(bean.getStatus()){
            toHander("true",type,Config.RESULT_SUCCESS);
        }else{
            toHander(error.getErr(),type,Config.RESULT_ERROR);
        }
    }

    private void checkData(String msg,int type) {
        RequestDataBean bean = gson.fromJson(msg,RequestDataBean.class);

        if(bean.getStatus()){
            toHander(bean,type,Config.RESULT_SUCCESS);
        }else{
            toHander(error.getErr(),type,Config.RESULT_ERROR);
        }
    }


    public void toHander(Object ob , int type ,int status){
        Message message = Message.obtain();
        message.what = type;
        message.arg1 = status;
        message.obj = ob;
        handler.sendMessage(message);
    }
}
