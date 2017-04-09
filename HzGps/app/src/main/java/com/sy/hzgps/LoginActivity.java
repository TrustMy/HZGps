package com.sy.hzgps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sy.hzgps.tool.AndroidCheckVersion;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        new AndroidCheckVersion(this).checkVersion();
    }

    public void toMain(View v)
    {
        startActivity(new Intent(this,MainActivity.class));
    }
}
