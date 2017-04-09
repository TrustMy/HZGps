package com.sy.hzgps.message;

/**
 * Created by jiayang on 2016/7/14.
 */
public enum ObdMessageID {

    UNKOWN((int)0),
    ENGINE_ON((int)1),
    ENGINE_OFF((int)2),
    OBD_REPORT((int)3),
    OVER_SPEED_ALARM((int)4),
    REGISTER((int)5),
    AUTHENTICATE((int)6),
    HEARTBEAT((int)7),
    TRIP_REPORT((int)8),
    COMMON_REPLY((int)9),
    QUERY_PARAMS_REPLY((int)10);

    private int value = 0;

    ObdMessageID(int value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public static ObdMessageID valueOf(int value) {    //    手写的从int到enum的转换函数
        switch (value) {
            case 0:
                return UNKOWN;
            case 1:
                return ENGINE_ON;
            case 2:
                return ENGINE_OFF;
            case 3:
                return OBD_REPORT;
            case 4:
                return OVER_SPEED_ALARM;
            case 5:
                return REGISTER;
            case 6:
                return AUTHENTICATE;
            case 7:
                return HEARTBEAT;
            case 8:
                return TRIP_REPORT;

            case 9:
                return COMMON_REPLY;
            case 10:
                return HEARTBEAT;

            default:
                return QUERY_PARAMS_REPLY;
        }
    }

    public int value() {
        return this.value;
    }
}
