package com.sy.hzgps;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.IBinder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.sy.hzgps.bean.QRcodeBean;
import com.sy.hzgps.database.DBHelperLH;
import com.sy.hzgps.database.DBManagerLH;
import com.sy.hzgps.getdata.GetDataActivity;
import com.sy.hzgps.server.MyService;
import com.sy.hzgps.tool.lh.AndroidCheckVersion;
import com.sy.hzgps.tool.lh.TimeTool;
import com.sy.hzgps.tool.dialog.DialogTool;
import com.sy.hzgps.tool.qrcode.QRcodeTool;

import java.util.List;

public class MainActivity extends BaseActivity {
    private MyService myServer = null;
    ImageView test;
    private TextView workMsg;
    private DBHelperLH dbHelperLH;
    private DBManagerLH dbManagerLH;

    private EditText editText;

    private String msg = "{name:张三,starttime:2017327,endtime:2047327,num:12345678912456789}";
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

        editText = findView(R.id.ed);


        Button start = findView(R.id.start);
        Button end = findView(R.id.end);
        setOnClick(start);
        setOnClick(end);

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
                            .getBitmap(),qRcodeBeen.get(0).getTime());
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
            String time = TimeTool.getSystemTime();
            Bitmap bitmap = QRcodeTool.getQRcode(msg,600);
            DialogTool.showDialog(this,R.layout.dialog_qr,bitmap, time);

            List<QRcodeBean> qRcodeBeens = dbManagerLH.select();
            if(qRcodeBeens!= null&& qRcodeBeens.size() != 0){
                dbManagerLH.update(bitmap,time);
            }else {
                dbManagerLH.add(bitmap,time);
            }


        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this,"二维码生成错误",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        /**
         * 设置edittext 可编辑和不可编辑
         */
        switch (view.getId()){
            case R.id.start:
//                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setEnabled(false);
                Toast.makeText(this,"start",Toast.LENGTH_LONG).show();
                break;
            case R.id.end:
//                editText.setInputType(InputType.TYPE_NULL);
                editText.setEnabled(true);
                Toast.makeText(this,"end",Toast.LENGTH_LONG).show();
                break;
        }
    }
}
