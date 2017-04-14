package com.sy.hzgps.request;

import android.content.Context;

import com.sy.hzgps.Config;
import com.sy.hzgps.tool.lh.L;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Trust on 2017/4/11.
 */
public class GetRequest {
    private Context context;
    private OkHttpClient okHttpClient;
    private Request.Builder builder;

    public GetRequest(Context context) {
        this.context = context;
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(Config.OUT_TIME, TimeUnit.SECONDS)
                .readTimeout(Config.OUT_TIME,TimeUnit.SECONDS)
                .build();
    }

    public void getUrl(String url){
        builder = new Request.Builder();

        final Request request = builder.get().url(url).build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                L.err("onFailure :"+e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body =  response.toString();
                L.d("onResponse:"+body);
            }
        });
    }




}
