package com.sy.hzgps.logregist;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.EditText;

import com.sy.hzgps.BaseActivity;
import com.sy.hzgps.Config;
import com.sy.hzgps.MainActivity;
import com.sy.hzgps.R;
import com.sy.hzgps.database.DBHelperLH;
import com.sy.hzgps.database.DBManagerLH;
import com.sy.hzgps.network.ca.CACommHelper;
import com.sy.hzgps.request.GetRequest;
import com.sy.hzgps.request.PostRequest;
import com.sy.hzgps.server.MyService;
import com.sy.hzgps.tool.PermissionUtils;
import com.sy.hzgps.tool.lh.AndroidCheckVersion;
import com.sy.hzgps.tool.lh.L;
import com.sy.hzgps.tool.lh.T;
import com.sy.hzgps.tool.sy.SysTools;

public class LoginActivity extends BaseActivity {


    private MyService myService = null;

    private EditText termIdEd,pwdEd;
    private DBHelperLH dbHelperLH;
    private DBManagerLH dbManagerLH;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Config.LOGING:
                    if(Config.LOGING_SUCCESS == msg.arg1){
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    }else{
                        if(msg.obj != null){
                            T.showToast(LoginActivity.this,(String)msg.obj);
                        }
                    }
                    break;
            }
        }
    };

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

            myService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个MsgService对象
            myService = ((MyService.MsgBinder) service).getService();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelperLH = new DBHelperLH(this);
        dbHelperLH.getWritableDatabase();
        dbManagerLH = new DBManagerLH(this);


        AndroidCheckVersion  androidVersion =new AndroidCheckVersion(this);
//        androidVersion.checkVersion();
        if(androidVersion.isLacksOfPermission(AndroidCheckVersion.PERMISSION[0])){
            L.d("6.0");
            init();
        }

        CACommHelper.loginHandler = handler;

        initView();

        if (myService == null) {

            Intent intent = new Intent(LoginActivity.this, MyService.class);

            bindService(intent, conn, Context.BIND_AUTO_CREATE);
        }

    }

    private void init() {
        PermissionUtils.requestMultiPermissions(this,mPermissionGrant);

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

        if(!temId.equals("") && !pwd.equals("") && myService!= null){
            myService.setReadyToRegister();
            String terminalId = null;
            for( int i = 0; i < 10 && terminalId == null; i++ ) {
                terminalId = SysTools.getTerminalId();


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            myService.registerToServer(temId,terminalId);
        }else {
            T.showToast(LoginActivity.this,"账号或密码有误!");
        }


//        getRequest.getUrl("http://www.apkbus.com/forum.php?mod=viewthread&tid=273228");
//        postRequest.postUrl("http://139.196.229.233:8080/EBWebServer-2.0/rest/gps/latest/");
//
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (myService == null) {

            Intent intent = new Intent(LoginActivity.this, MyService.class);

            bindService(intent, conn, Context.BIND_AUTO_CREATE);
        }
        L.d("permissions:"+permissions.toString()+"|requestCode:"+requestCode+"|grantResults:"+grantResults.toString());
    }
}
