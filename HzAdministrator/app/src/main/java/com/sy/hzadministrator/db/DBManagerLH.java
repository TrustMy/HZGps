package com.sy.hzadministrator.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import com.sy.hzadministrator.L;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    public  boolean add(Map<String,Object> map){
        if( map != null){

            contentValues.put("termId",(Integer) map.get("termId"));
//            contentValues.put("qR",os.toByteArray());
            contentValues.put("time",(String)map.get("time"));
            contentValues.put("startName",(String)map.get("startName"));
            contentValues.put("endName",(String)map.get("endName"));
            contentValues.put("orderNumble",(String)map.get("order"));
            contentValues.put("startTime",map.get("startTime").toString());
            contentValues.put("endTime",(String)map.get("endTime").toString());
            contentValues.put("workTime",map.get("workTime").toString());
            contentValues.put("generatePictureTime",
                    map.get("generatePictureTime").toString());
            contentValues.put("status",(Integer) map.get("status"));
            dbWrit.insert("history",null,contentValues);
            contentValues.clear();
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
            dbWrit.update("order",contentValues,"name = ?",
                    new String[]{"ErWeiMa"});
            contentValues.clear();
            L.d("update success!");
            return true;
        }
        return false;
    }

    public List<OrderBean> select (){
        List<OrderBean> ml = new ArrayList<>();
        Cursor cursor = dbWrit.query("history",null,null,null,null,null,null);
        OrderBean qRcodeBean = null;

        if(cursor.moveToFirst()){
            do {
                L.d("select");

                int termId = cursor.getInt(cursor.getColumnIndex("termId"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String orderName = cursor.getString(cursor.getColumnIndex("orderNumble"));
                String startName = cursor.getString(cursor.getColumnIndex("startName"));
                String endName = cursor.getString(cursor.getColumnIndex("endName"));
                String worktime = cursor.getString(cursor.getColumnIndex("workTime"));
                int status = cursor.getInt(cursor.getColumnIndex("status"));
                long startTime =  Long.
                        parseLong(cursor.getString(cursor.getColumnIndex("startTime")));
                long endTime =  Long.
                        parseLong(cursor.getString(cursor.getColumnIndex("endTime")));
                long generatePictureTime = Long.
                        parseLong(cursor.getString(cursor.getColumnIndex("generatePictureTime")));
                ml.add(new OrderBean(orderName,startName,endName,time,null,termId,worktime,
                        startTime,endTime,generatePictureTime,status));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return ml;
    }

    public boolean del(){
        dbWrit.delete("order","name = ?",new String[]{"ErWeiMa"});
        return false;
    }


    public boolean updateOrder(String order,int status){
        if(order != null){

            contentValues.put("status",status);
            dbWrit.update("history",contentValues,"orderNumble = ?",
                    new String[]{order});
            contentValues.clear();
            L.d("updateOrder success!");
            return true;
        }
        return false;
    }


    /**
     * 通过订单号查有没有这条信息
     * @param order
     * @return
     */
    public String selectFirstDate(String order){
        String selection = "orderNumble= ?" ;
        String[] selectionArgs = new  String[]{ order };
        String termId = null;
        Bitmap bitmap = null;
        if(!order.equals("")){
            Cursor cursor = dbWrit.query("history",null,selection,selectionArgs,null,null,null);
            if(cursor.moveToFirst()){
                do {
                    termId = cursor.getString(cursor.getColumnIndex("termId"));
                }while (cursor.moveToNext());
            }
            cursor.close();
            return termId;
        }else{
            return null;
        }
    }

}
