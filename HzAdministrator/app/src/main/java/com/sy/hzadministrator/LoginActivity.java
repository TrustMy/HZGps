package com.sy.hzadministrator;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;

import com.sy.hzadministrator.request.PostRequest;

public class LoginActivity extends BaseActivity {

    private EditText termIdEd,pwdEd;
    private PostRequest postRequest;
    private Handler logHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Config.LOGING:
                    if(msg.arg1 == Config.RESULT_SUCCESS){
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    }else{
                        T.showToast(LoginActivity.this,(String)msg.obj);
                    }
                    break;

            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);





        init();
        initView();


    }

    private void init() {
        postRequest = new PostRequest(LoginActivity.this,logHandler);

        quanxian();
    }

    private void quanxian() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    1);}
    }

    private void initView() {

        termIdEd = findView(R.id.loging_phone);
        pwdEd = findView(R.id.loging_pwd);
    }




    public void toMain(View v)
    {
        T.showToast(LoginActivity.this,"登录中...");
        String temId = termIdEd.getText().toString().trim();
        String pwd = pwdEd.getText().toString().trim();

        postRequest.loging(Server.Server+Server.Loging,temId,pwd,Config.LOGING);

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
