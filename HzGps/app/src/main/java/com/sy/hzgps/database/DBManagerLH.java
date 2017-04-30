package com.sy.hzgps.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sy.hzgps.Config;
import com.sy.hzgps.bean.GpsBean;
import com.sy.hzgps.bean.OrderBean;
import com.sy.hzgps.bean.QRcodeBean;
import com.sy.hzgps.message.ObdMessage;
import com.sy.hzgps.tool.lh.L;

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
            contentValues.clear();
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



    public boolean addOrder(Map<String,Object> map){
        if(map != null){
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Bitmap bitmap = (Bitmap) map.get("qR");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            contentValues.put("termId",(Integer) map.get("termId"));
            contentValues.put("qR",os.toByteArray());
            contentValues.put("time",(String)map.get("time"));
            contentValues.put("startName",(String)map.get("startName"));
            contentValues.put("endName",(String)map.get("endName"));
            contentValues.put("orderNumble",(String)map.get("order"));
            contentValues.put("startTime",(String)map.get("startTime"));
            contentValues.put("endTime",(String)map.get("endTime"));
            contentValues.put("generatePictureTime",(String)map.get("generatePictureTime"));
            contentValues.put("orderPhotoBit", (String) map.get("orderPhotoBit"));

            L.d("map.get(\"status\"):"+(Integer)map.get("status"));
            L.d("addOrder startName:"+map.get("startName"));
            contentValues.put("status",(Integer) map.get("status"));
            dbWrit.insert("history",null,contentValues);
            contentValues.clear();
            L.d("addOrder: success");
            return  true;
        }
        return false;
    }

    public List<OrderBean> selectOrder(){
        List<OrderBean> ml = new ArrayList<>();
        Cursor cursor = dbWrit.query("history",null,null,null,null,null,null);
        OrderBean orderBean = null;

        if(cursor.moveToFirst()){
            do {
                Bitmap bitmap = null;
                String orderPhotoBit = null;
                String time = cursor.getString(cursor.getColumnIndex("time"));
                int termId = cursor.getInt(cursor.getColumnIndex("termId"));
                String order = cursor.getString(cursor.getColumnIndex("orderNumble"));
                String startName = cursor.getString(cursor.getColumnIndex("startName"));
                String endName = cursor.getString(cursor.getColumnIndex("endName"));

                int status = cursor.getInt(cursor.getColumnIndex("status"));
                if(status != Config.SAVE_STATUS_SUCCESS){
                    byte[] bmp = cursor.getBlob(cursor.getColumnIndex("qR"));
                    bitmap = BitmapFactory.decodeByteArray(bmp,0,bmp.length);
                    orderPhotoBit = cursor.getString(cursor.getColumnIndex("orderPhotoBit"));
                }
                long startTime =  Long.
                        parseLong(cursor.getString(cursor.getColumnIndex("startTime")));
                long endTime =  Long.
                        parseLong(cursor.getString(cursor.getColumnIndex("endTime")));
                long generatePictureTime = Long.
                        parseLong(cursor.getString(cursor.getColumnIndex("generatePictureTime")));
                L.d("status selectOrder:"+status);
                L.d("selectOrder startTime:"+startTime);
                ml.add(new OrderBean(order,startName,endName,time,bitmap,termId,status,startTime,
                        endTime,generatePictureTime,orderPhotoBit));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return ml;
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
     * 通过订单号查 二维码
     * @param order
     * @return
     */
    public Bitmap selectFirstDate(String order){
        String selection = "orderNumble=?" ;
        String[] selectionArgs = new  String[]{ order };
        Bitmap bitmap = null;
        if(!order.equals("")){
            Cursor cursor = dbWrit.query("history",null,selection,selectionArgs,null,null,null);
            if(cursor.moveToFirst()){
                do {
                    byte[] bmp = cursor.getBlob(cursor.getColumnIndex("qR"));
                    bitmap = BitmapFactory.decodeByteArray(bmp,0,bmp.length);
                }while (cursor.moveToNext());
            }
            cursor.close();
            return bitmap;
        }else{
            return null;
        }
    }


    /**
     * 保存gps 数据
     * @param
     * @param time
     * @return
     */
    public  void addGps(double lat,double lon,long time,int carStatus){
            contentValues.put("lat",lat+"");
            contentValues.put("lon",lon+"");
            contentValues.put("time",time+"");
            contentValues.put("carStatus",carStatus);
            dbWrit.insert("gpsHistory",null,contentValues);
            contentValues.clear();
            L.d("add  GPS success!");
            contentValues.clear();

    }

    /**
     * 查询gps 坐标
     * @return
     */
    public List<GpsBean> selectGps (){
        List<GpsBean> ml = new ArrayList<>();
        Cursor cursor = dbWrit.query("gpsHistory",null,null,null,null,null,null);

        if(cursor.moveToFirst()){
            do {
                L.d("select");
                double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex("lat")));
                int carStatus = cursor.getColumnIndex("carStatus");
                double lon = Double.parseDouble(cursor.getString(cursor.getColumnIndex("lon")));
                long time = Long.parseLong(cursor.getString(cursor.getColumnIndex("time")));
                ml.add(new GpsBean(lat,lon,time,carStatus));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return ml;
    }


    public void delGps(){
        dbWrit.delete("gpsHistory",null,null);
        L.d("delGps suceess!");
    }
}
