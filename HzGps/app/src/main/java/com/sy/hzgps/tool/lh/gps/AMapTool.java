package com.sy.hzgps.tool.lh.gps;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.sy.hzgps.tool.lh.L;


/**
 * Created by Trust on 2017/3/3.
 */
public class AMapTool {

   public static boolean showMark(Context context, AMap aMap, LatLng latLng ,int icon ,String title,String msg,boolean needCenter)
    {
        if(latLng.longitude != 0.0)
        {
            aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    new LatLng(latLng.latitude,latLng.longitude),//新的中心点坐标
                    15, //新的缩放级别
                    0, //俯仰角0°~45°（垂直与地图时为0）
                    0  ////偏航角 0~360° (正北方为0)
            )));

            if(msg != null && !msg.equals(""))
            {
                aMap.addMarker(new MarkerOptions().
                        position(latLng).
                        title(title)
                        .icon( BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                .decodeResource(context.getResources(),
                                        icon))).
                                snippet(msg)).showInfoWindow();
            }else
            {
                L.err("showMark: msg == null ");
            }
            return true;
        }else
        {
            L.err("showMark :latLng == null   ||   latLng.longitude == 0.0");
            return false;
        }

    }


    public static  boolean showMark(Context context,AMap aMap,LatLng latLng,int icon,String title,String msg)
    {
        if(latLng.longitude != 0.0)
        {
            aMap.addMarker(new MarkerOptions().
                    position(latLng).
                    title(title).
                    snippet(msg).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.
                    decodeResource(context.getResources(),icon))));
            return true;
        }else
        {
            return false;
        }
    }




    public static void  aMapClear(AMap aMap)
    {
        if(aMap != null)
        {
            aMap.clear();
        }else
        {
            L.err("aMapClear: aMap == null  ");
        }
    }

}
