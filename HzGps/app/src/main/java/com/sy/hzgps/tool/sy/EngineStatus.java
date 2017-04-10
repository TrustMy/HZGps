package com.sy.hzgps.tool.sy;

/**
 * Created by jiayang on 2016/7/12.
 */
public enum EngineStatus {
    ACC_OFF((int)0),
    FIRE_ON((int)1),
    FIRE_OFF((int)2),
    ACC_ON((int)4);

    private int value = 0;

    EngineStatus(int value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public static EngineStatus valueOf(int value) {    //    手写的从int到enum的转换函数
        switch (value) {
            case 0:
                return ACC_OFF;
            case 1:
                return FIRE_ON;
            case 2:
                return FIRE_OFF;
            case 4:
                return ACC_ON;
            default:
                return ACC_OFF;
        }
    }

    public int value() {
        return this.value;
    }
}
