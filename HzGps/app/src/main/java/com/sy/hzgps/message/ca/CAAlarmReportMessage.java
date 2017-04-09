package com.sy.hzgps.message.ca;



import com.sy.hzgps.message.GpsMessage;
import com.sy.hzgps.message.ObdMessageID;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiayang on 2016/7/28.
 */
public class CAAlarmReportMessage extends GpsMessage {

    private Map<String, Object> alarmMaps = new HashMap<String, Object>();

    public void setAlarmValues( Map<String, Object> v) {
        alarmMaps = v;
    }

    public Map<String, Object> getAlarmValues() {
        return alarmMaps;
    }

    public void addConent(String key, Object o) {
        alarmMaps.put(key, o);
    }


    public CAAlarmReportMessage(ObdMessageID messageId, boolean needSave) {
        super(messageId, needSave);
    }

    @Override
    public String toString() {
        String alarmInfo = super.toString();

        for(String key: alarmMaps.keySet()) {
            alarmInfo += " " + key + ":" + alarmMaps.get(key);
        }

        return alarmInfo;
    }
}
