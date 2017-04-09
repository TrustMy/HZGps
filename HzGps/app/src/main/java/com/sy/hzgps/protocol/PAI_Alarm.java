package com.sy.hzgps.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ALARM 数据附加项
 * @author linsen
 *
 */

public  class PAI_Alarm implements IAdditionalItem {
	
	private final int nArrySize=16;
	
	public static final Logger logger = LoggerFactory.getLogger(PAI_Alarm.class);
	
	public PAI_Alarm() {
		for(int i=0;i<nArrySize;i++) {
			strArryItem[i]="";
		}

	}

	private String[]strArryItem =  new String[nArrySize];
	
	public byte getAdditionalId(){
		return (byte)0xE3;	
	}

	public byte getAdditionalLength( ){
		String content = "";

		for(String s : strArryItem) {
			if ( s != null ) {
				content += s;
			}
			content += ";";
		}
		content = content.substring(0, content.length() - 1);
		return (byte) content.length();
		
	}

	public byte[] writeToBytes(){
		String content = "";

		for(String s : strArryItem) {
			if ( s != null ) {
				content += s;
			}
			content += ";";
		}

		content = content.substring(0, content.length() - 1);

		//logger.info("Content = " + content);
		return content.getBytes();
	}

	public void readFromBytes(byte[] bytes){
		MyBuffer buff = new MyBuffer(bytes);

		String content = buff.getString();
		
		String[] tmp = content.split(";");
		System.arraycopy(tmp, 0, strArryItem, 0, tmp.length);
		
	}
	
	
	public void  print_arrString() {
		
		for(String s:strArryItem)
	    	logger.info(s);
		
	}
	
	/**
	 * 第1项  ALARM
	 * 超速
	 * @param status
	 *  @param maxSpeed
	 */
	public void addOverSpeedAlarm(int status, float maxSpeed ){	
		strArryItem[0] = String.valueOf(status) + "," + String.valueOf(maxSpeed);
	}

	
	/**
	 * 第2项  ALARM
	 * 超转速
	 * @param status
	 * @param delay
	 *  @param maxRpm
	 */
	public void addOverRpmAlarm(int status, int delay, int maxRpm) {
		strArryItem[1] = String.valueOf(status) +  "," + String.valueOf(delay)+ "," + String.valueOf(maxRpm);
	}
	
	/**
	 * 第3项  ALARM
	 * 低电压	
	 *  @param battValue
	 */
	public void addLowBattAlarm (float battValue) {
       strArryItem[2] = String.valueOf(battValue);
	}
	
	/**
	 * 第4项  ALARM
	 * 馈电
	 *  @param battValue
	 */
	public void  addVeryLowBattAlarm(float battValue) {
		   strArryItem[3] = String.valueOf(battValue);
	}
	
	
	/**
	 * 第5项  ALARM
	 * 震动报警	
	 */
	public void  addShakeAlarm() {
	       strArryItem[4] = String.valueOf(1);
	}
	
	/**
	 * 第6项  ALARM
	 *水温过高	
	 *  @param tempValue
	 */
	public void addCoolTempratureAlarm(int tempValue) {
		   strArryItem[5] = String.valueOf(tempValue);
	}
	
	/**
	 * 第7项  ALARM
	 * 油量不足	
	 *  @param gasLevelValue
	 */
	
	public void addLowGasLevelAlarm(int gasLevelValue) {
		   strArryItem[6]= String.valueOf(gasLevelValue);
	}
	
	/**
	 * 第8项  ALARM
	 * 故障灯亮	
	 */
	public void addMILAlarm() {
	       strArryItem[7] = String.valueOf(1);
	}
	
	/**
	 * 第9项  ALARM
	 * 油量异常（多 /少）
	 * @param status
	  * @param oriValue
	 *  @param curValue
	 */
	public void  addGasLevelChangeAlarm(int status, int oriValue,int curValue) {
		strArryItem[8] = String.valueOf(status) + "," + String.valueOf(oriValue) + ","+ String.valueOf(curValue);
	}
	
	/**
	 * 第10项  ALARM
	 * 怠速运转
	 * @param status
	 *  @param delay
	 */
	public void addLongIdleRunAlarm(int status, int delay) {
		   strArryItem[9] = String.valueOf(status) + "," + String.valueOf(delay);
	}
	
	/**
	 * 第11项  ALARM
	 * 设备拔出	
	 */
	public void  addDevPlugOutAlarm() {
		  strArryItem[10] = String.valueOf(1);
	}
	
	/**
	 * 第12项  ALARM
	 * 电子围栏	
	 * @param status
	 *  @param fenceID
	 */
	public void  addGeoFenceAlarm(int status, int fenceID) {
		   strArryItem[11] = String.valueOf(status)  + "," + String.valueOf(fenceID);
	}
	
	
	/**
	 * 第13项  ALARM
	 * 急加速减速	
	 * @param status
	 *  @param accValue
	 */
	public void addRaDeAccelerationAlarm(int status, float accValue) {
		   strArryItem[12] = String.valueOf(status) + "," + String.valueOf(accValue);
	}
	
	/**
	 * 第14项  ALARM
	 * 急转弯
	 * @param status
	 *  @param wheelValue
	 */
	public void addRaidWheelRunAlarm(int status, float wheelValue) {
		   strArryItem[13] = String.valueOf(status) + "," + String.valueOf(wheelValue);
	}
	
	
	/**
	 * 第15项  ALARM
	 *行车碰撞
	 */
	public void addCarCrushAlarm() {
		   strArryItem[14] = String.valueOf(1);
	}
	
	/**
	 * 第16项  ALARM
	 * 疲劳驾驶
	 * @param
	 *  @param
	 */
	public void  addTiredDriving(int nsec) {
		   strArryItem[15] = String.valueOf(nsec);
	}
	
	
	
	@Override
	public String toString() {
		String content = "";

		for(int i = 0; i < nArrySize; i++) {
			
			if ( strArryItem[i] != null ) {
				content += strArryItem[i];
			}
			content += ";";
		}
		content = content.substring(0, content.length() - 1);
		return content;
	}
	
}