package com.sy.hzadministrator.request;

import android.content.Context;
import android.os.Handler;
import android.os.Message;


import com.sy.hzadministrator.Config;
import com.sy.hzadministrator.L;
import com.sy.hzadministrator.request.ssl.TrustAllCerts;

import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Trust on 2017/4/11.
 */
public class PostRequest {
    private Context context;
    private OkHttpClient okHttpClient;
    private Request.Builder builder;
    private PostNet postNet;
    public PostRequest(Context context ,Handler handler) {
        this.context = context;
        this.postNet = new PostNet(handler);
        this.okHttpClient = new OkHttpClient.Builder()
                .readTimeout(Config.OUT_TIME, TimeUnit.SECONDS)
                .connectTimeout(Config.OUT_TIME,TimeUnit.SECONDS)
                .sslSocketFactory(TrustAllCerts.createSSLSocketFactory(),new TrustAllCerts())
                .hostnameVerifier(new TrustAllCerts.TrustAllHostnameVerifier())
                .build();
    }


    public void requestOrder(String url, Map<String,Object> map,int type){
        JSONObject jsonObject = new JSONObject(map);
        setHeaders(url,jsonObject.toString(),type);
    }

    private void setHeaders(String url,String json,int type) {
        MediaType JSON = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JSON, json);
        builder = new Request.Builder();
        Request request = builder.url(url).post(body).build();
        callBack(request ,type);
    }

    public void postUrl(String url,int type){
        String json = "{\"termId\":"+"123456789"+"};";

        MediaType JSON = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JSON, json);

        builder = new Request.Builder();
        Request request = builder.url(url).addHeader("Token", "12312313123").post(body).build();

        callBack(request,type);
    }

    public void loging(String url,String phone,String pwd,int type){
        FormBody body = new FormBody.Builder()
                .add("account", phone)
                .add("passWord", pwd)
                .build();
        builder = new Request.Builder();
        Request request = builder.url(url).post(body).build();
        callBack(request,type);
    }

    private void callBack(Request request, final int type) {
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                L.err("onFailure :"+e.toString());

                Message message = Message.obtain();
                message.what = type;
                message.arg1 = Config.RESULT_ERROR;
                message.obj = "{\n" +
                        "  \"status\":false,\"err\":\"网络错误\"\n" +
                        "}";
                postNet.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body =  response.body().string();
                L.d("onResponse:"+body);
                Message message = Message.obtain();
                message.what = type;
                if(response.code() == 200){
                    message.arg1 = Config.RESULT_SUCCESS;
                    message.obj = body;
                }else{
                    message.arg1 = Config.RESULT_ERROR;
                    message.obj = "err";
                }

                postNet.sendMessage(message);
            }
        });
    }
}
