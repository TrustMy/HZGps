package com.sy.hzgps.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.sy.hzgps.bean.QRcodeBean;
import com.sy.hzgps.tool.L;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trust on 2017/4/8.
 */

public class DBManagerLH {
    private DBHelperLH dbHelperLH;
    private SQLiteDatabase dbWrit;
    private SQLiteDatabase dbRead;
    private ContentValues contentValues;
    public DBManagerLH(Context context) {
        dbHelperLH = new DBHelperLH(context);
        dbWrit = dbHelperLH.getWritableDatabase();
        dbRead = dbHelperLH.getReadableDatabase();
        contentValues = new ContentValues();
    }


    public  boolean add(Bitmap bitmap,String time){
        if(bitmap != null && time != null){
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            contentValues.put("name","ErWeiMa");
            contentValues.put("img",os.toByteArray());
            contentValues.put("time",time);
            dbWrit.insert("Img",null,contentValues);
//            contentValues.clear();
            L.d("add  success!");
            return true;
        }
        return  false;
    }

    public boolean update(Bitmap bitmap,String time){
        if(bitmap != null && time != null){
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            contentValues.put("img",os.toByteArray());
            contentValues.put("time",time);
            dbWrit.update("Img",contentValues,"name = ?",
                    new String[]{"ErWeiMa"});
            contentValues.clear();
            L.d("update success!");
            return true;
        }
        return false;
    }

    public List<QRcodeBean> select (){
        List<QRcodeBean> ml = new ArrayList<>();
        Cursor cursor = dbWrit.query("Img",null,null,null,null,null,null);
        QRcodeBean qRcodeBean = null;

        if(cursor.moveToFirst()){
            do {
                L.d("select");
                byte[] bmp = cursor.getBlob(cursor.getColumnIndex("img"));
                Bitmap bitmap = BitmapFactory.decodeByteArray(bmp,0,bmp.length);
                String time = cursor.getString(cursor.getColumnIndex("time"));
                ml.add(new QRcodeBean(bitmap,time));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return ml;
    }

    public boolean del(){
        dbWrit.delete("Img","name = ?",new String[]{"ErWeiMa"});
        return false;
    }
}
