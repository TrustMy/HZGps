package com.sy.hzgps.request;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.gsm.GsmCellLocation;

import com.google.gson.Gson;
import com.sy.hzgps.Config;
import com.sy.hzgps.bean.Error;
import com.sy.hzgps.bean.RequestDataBean;
import com.sy.hzgps.tool.lh.L;

/**
 * Created by Trust on 2017/4/13.
 */
public class PostNet extends Handler {
    private Handler handler;
    private Gson gson;
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
                    toHander((String)msg.obj,Config.ORDER,Config.RESULT_ERROR);
                }
                break;

            case Config.OUT_TIME:
                if(msg.arg1 == Config.RESULT_SUCCESS){
                    test(msg.obj.toString(),Config.OUT_TIME);

                }else{
                    toHander((String)msg.obj,Config.OUT_TIME,Config.RESULT_ERROR);
                }
                break;
        }
    }

    private void test(String msg ,int type) {
        Message mes = new Message();
        mes.what = type;
        mes.arg1 = Config.RESULT_SUCCESS;
        mes.obj = msg;
        handler.sendMessage(mes);
    }

    private void checkData(String msg,int type) {
        RequestDataBean bean = gson.fromJson(msg,RequestDataBean.class);
        Error error = gson.fromJson(msg,Error.class);
        if(bean.getStatus()){
            toHander("true",type,Config.RESULT_SUCCESS);
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
