package com.sy.hzadministrator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by Trust on 2017/3/27.
 */
public class BaseActivity extends AppCompatActivity implements View.OnClickListener{
    private SparseArray<View> mViews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mViews = new SparseArray<>();
    }

    public <E extends View>E findView(int viewId){
        E view = (E) mViews.get(viewId);
        if(view == null)
        {
            view = (E) findViewById(viewId);
            mViews.put(viewId,view);
        }
        return view;
    }

    public <E extends View >void setOnClick(E view){
        view.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

    }
}
