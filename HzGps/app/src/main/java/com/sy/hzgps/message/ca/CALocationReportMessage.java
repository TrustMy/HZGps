package com.sy.hzgps.message.ca;


import com.sy.hzgps.message.GpsMessage;
import com.sy.hzgps.message.ObdMessageID;

import java.util.Locale;

/**
 * Created by jiayang on 2016/7/14.
 */
public class CALocationReportMessage extends GpsMessage {

    /**
     * RPM当前转速
     */
    private Short rpm = null;

    public void setRpm(Short v) {
        rpm = v;
    }

    public Short getRpm() {
        return rpm;
    }

    /**
     * OBD测得的速度，单位: km/h
     */
    private Float obdSpeed = null;

    public void setObdSpeed(Float v) {
        obdSpeed = v;
    }

    public Float getObdSpeed() {
        return obdSpeed;
    }

    /**
     * 电池电压
     */
    private Float batteryVoltage = null;

    public void setBatteryVoltage(Float v) {
        batteryVoltage = v;
    }

    public Float getBatteryVoltage() {
        return batteryVoltage;
    }

    /**
     * 里程
     */
    private Float tripDistance = null;

    public void setTripDistance(Float v) {
        tripDistance = v;
    }

    public Float getTripDistance() {
        return tripDistance;
    }

    /**
     * 仪表盘里程
     */
    private Float dashBoardDistance = null;

    public void setDashBoardDistance(Float v) {
        dashBoardDistance = v;
    }

    public Float getDashBoardDistance() {
        return dashBoardDistance;
    }


    /**
     * 冷却液温度
     */
    private Short coolantTemperature = null;

    public void setCoolantTemperature(Short v) {
        coolantTemperature = v;
    }

    public Short getCoolantTemperature() {
        return coolantTemperature;
    }


    /**
     * 发动机负荷
     */
    private Byte enginePayload = null;

    public void setEnginePayload(Byte v) {
        enginePayload = v;
    }

    public Byte getEnginePayload() {
        return enginePayload;
    }

    /**
     * 燃油百分比
     */
    private Short fuelPct = null;

    public void setFuelPct(Short v) {
        fuelPct = v;
    }

    public Short getFuelPct() {
        return fuelPct;
    }

    /**
     * 燃油压力
     */
    private Short fuelPressure = null;

    public void setFuelPressure(Short v) {
        fuelPressure = v;
    }

    public Short getFuelPressure() {
        return fuelPressure;
    }

    /**
     * 进气歧管绝对压力
     */
    private Short IMAPressure = null;

    public void setIMAPressure(Short v) {
        IMAPressure= v;
    }

    public Short getIMAPressure() {
        return IMAPressure;
    }

    /**
     * 进气温度
     */
    private Short intakeAirTemperature = null;

    public void setIntakeAirTemperature(Short v) {
        intakeAirTemperature = v;
    }

    public Short getIntakeAirTemperature() {
        return intakeAirTemperature;
    }

    /**
     * 进气流量
     */
    private Short massAirFlow = null;

    public void setMassAirFlow(Short v) {
        massAirFlow = v;
    }

    public Short getMassAirFlow() {
        return massAirFlow;
    }

    /**
     * 节气门绝对位置
     */
    private Byte throttlePos;

    public void setThrottlePos(Byte v) {
        throttlePos = v;
    }

    public Byte getThrottlePos() {
        return throttlePos;
    }


    public CALocationReportMessage(ObdMessageID messageId, boolean needSave) {
        super(messageId, needSave);
    }

    @Override
    public String toString() {
        String obdInfo = String.format(Locale.CHINESE, " 转速 = %d, 速度 = %f, 电压 = %f, 里程 = %f, 仪表盘里程 = %f, 冷却液温度 =  %d, " +
                "引擎负荷 = %d, 燃油百分比 = %d, 燃油压力 = %d, 进气歧管绝对压力 = %d, 进气温度 = %d, 进气流量 = %d, 节气门绝对位置 = %d",
                rpm, obdSpeed, batteryVoltage, tripDistance, dashBoardDistance, coolantTemperature, enginePayload,
                fuelPct, fuelPressure, IMAPressure, intakeAirTemperature, massAirFlow, throttlePos);

        return super.toString() + obdInfo;
    }
}
