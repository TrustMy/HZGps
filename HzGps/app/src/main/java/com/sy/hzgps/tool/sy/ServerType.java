package com.sy.hzgps.tool.sy;

/**
 * Created by jiayang on 2016/7/12.
 */
public enum ServerType {
    REG_SERVER((int)0),
    MAIN_SERVER((int)1),
    BACKUP_SERVER((int)2);


    private int value = 0;

    ServerType(int value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public static ServerType valueOf(int value) {    //    手写的从int到enum的转换函数
        switch (value) {
            case 0:
                return REG_SERVER;
            case 1:
                return MAIN_SERVER;
            case 2:
                return BACKUP_SERVER;

            default:
                return MAIN_SERVER;
        }
    }

    public int value() {
        return this.value;
    }
}
