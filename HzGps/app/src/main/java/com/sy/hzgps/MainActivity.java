package com.sy.hzgps;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.icu.text.BreakIterator;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.sy.hzgps.bean.QRcodeBean;
import com.sy.hzgps.database.DBHelperLH;
import com.sy.hzgps.database.DBManagerLH;
import com.sy.hzgps.getdata.GetDataActivity;
import com.sy.hzgps.server.MyService;
import com.sy.hzgps.tool.AndroidCheckVersion;
import com.sy.hzgps.tool.L;
import com.sy.hzgps.tool.dialog.DialogTool;
import com.sy.hzgps.tool.qrcode.QRcodeTool;

import java.lang.ref.ReferenceQueue;
import java.util.List;

public class MainActivity extends BaseActivity {
    private MyService myServer = null;
    ImageView test;
    private TextView workMsg;
    private DBHelperLH dbHelperLH;
    private DBManagerLH dbManagerLH;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AndroidCheckVersion(this).checkVersion();

        if(myServer == null)
        {
            bindService(new Intent(MainActivity.this,MyService.class),serviceConnection, Context.BIND_AUTO_CREATE);
        }

        test = (ImageView) findViewById(R.id.test);
        initView();
    }



    private void initView() {
        dbHelperLH = new DBHelperLH(this);
        dbHelperLH.getWritableDatabase();
        dbManagerLH = new DBManagerLH(this);

        workMsg = findView(R.id.main_work_msg);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            myServer = ((MyService.MsgBinder)iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            myServer = null;
        }
    };


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void startGps(View v)
    {
        myServer.startWorking();
    }

    public void endGps(View v)
    {
        myServer.stopWorking();
        setQRcode();
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String s = getIntent().getStringExtra("test");
        if(s != null){
            workMsg.setText(s);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 1){
            String t = data.getStringExtra("startTime");
            workMsg.setText(t);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.main_register:
                Toast.makeText(this,"注册",Toast.LENGTH_LONG).show();


                break;
            case R.id.main_erweima:
                List<QRcodeBean> qRcodeBeen = dbManagerLH.select();

                if(qRcodeBeen.size() == 0){
                   Toast.makeText(this,"未找到二维码,请确认是否生成!",Toast.LENGTH_LONG).show();
                }else{
//                    test.setImageBitmap(qRcodeBeen.get(0).getBitmap());
                    DialogTool.showDialog(this,R.layout.dialog_qr,qRcodeBeen.get(0)
                            .getBitmap(),"shijian");
                }
                break;
            case R.id.main_getdata:
//                startActivity(new Intent(MainActivity.this, GetDataActivity.class));
                startActivityForResult(new Intent(MainActivity.this, GetDataActivity.class),1);

                break;
        }
        return true;
    }

    private void setQRcode() {
        try {
            Bitmap bitmap = QRcodeTool.getQRcode("{name:张三,starttime:2017327,endtime:2047327,num:12345678912456789}",400);
            DialogTool.showDialog(this,R.layout.dialog_qr,bitmap,"2017/4/9");

            List<QRcodeBean> qRcodeBeens = dbManagerLH.select();
            if(qRcodeBeens!= null&& qRcodeBeens.size() != 0){
                dbManagerLH.update(bitmap,"098");
            }else
            {
                dbManagerLH.add(bitmap,"123");
            }


        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this,"二维码生成错误",Toast.LENGTH_LONG).show();
        }
    }


}
