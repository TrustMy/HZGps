package com.sy.hzgps.tool.lh.gps;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.sy.hzgps.R;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trust on 2016/12/18.
 */
public class GPSHistoryLine {
    private AMap aMap;
    private List<LatLng> latLngs = new ArrayList<LatLng>();
    private List<BitmapDescriptor> texTuresList;
    private Context context;

    public GPSHistoryLine(AMap aMap , Context context ) {
        this.aMap = aMap;
        this.context = context;
        texTuresList = new ArrayList<BitmapDescriptor>();
        texTuresList.add(BitmapDescriptorFactory.fromResource(R.drawable.road_1));
    }

    public void setLatLngs(List<LatLng> latLngs) {
        this.latLngs = latLngs;
    }

    public void startHistory(String startName, String endName)
    {
        aMap.clear();

        aMap.addPolyline(new PolylineOptions().
                addAll(latLngs).width(30).color(Color.parseColor("#020176")).setCustomTextureList(texTuresList));

        AMapTool.showMark(context,aMap,latLngs.get(0),0,"当前位置",null,true);

        if(startName != null && endName != null)
        {
            AMapTool.showMark(context,aMap,latLngs.get(0),R.mipmap.ic_launcher,"起点",startName);
            AMapTool.showMark(context,aMap,latLngs.get(latLngs.size()-1),R.mipmap.ic_launcher,"终点",endName);
        }

    }





}
