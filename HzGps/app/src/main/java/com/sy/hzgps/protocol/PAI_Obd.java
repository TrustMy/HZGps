package com.sy.hzgps.protocol;



import com.sy.hzgps.tool.sy.Tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Obd数据附加项
 * @author jiayang
 *
 */
public class PAI_Obd implements IAdditionalItem {

	public static final Logger logger = LoggerFactory.getLogger(PAI_Obd.class);
	
	private final int nArraySize=13;
	
	public PAI_Obd() {
		for( int i = 0; i < nArraySize; i++ ) {
			itemArray[i] = "";
		}
	}
	
	/**
	 * Obd数据组
	 */
	private String[] itemArray = new String[nArraySize];
	
	public byte getAdditionalId() {
		return (byte) 0xE2;
	}

	public byte getAdditionalLength() {
		
		String content = "";

		for(String s : itemArray) {
			if ( s != null ) {
				content += s;
			}
			content += ";";
		}
		content = content.substring(0, content.length() - 1);
		return (byte) content.length();
	}

	
	/**
	 * 第1项
	 * RPM	
	 * @param rpm
	 */
	public void addRpm(int rpm) {
		itemArray[0] = String.valueOf(rpm);
	}
	
	/**
	 * 第2项
	 * 车速，单位：km/h
	 * @param speed
	 */
	public void addSpeed(float speed) {
		itemArray[1] = String.valueOf(speed);
	}
	
	/**
	 * 第3项
	 * 电池电压，单位：v
	 * @param voltage
	 */
	public void addVoltage(float voltage) {
		itemArray[2] = String.valueOf(voltage);
	}
		
	/**
	 * 第4项
	 * 行驶里程，单位：km
	 * @param trip
	 */
	public void addTrip(float trip) {
		itemArray[3] = String.valueOf(trip);
	}
	
	/**
	 * 第5项
	 * 仪表盘行驶里程，单位：km
	 * @param dashBoardTrip
	 */
	public void addDashBoardTrip(float dashBoardTrip) {
		itemArray[4] = String.valueOf(dashBoardTrip);
	}
	
	/**
	 * 第6项
	 * 冷却液温度
	 * @param coolantTemp
	 */
	public void addCoolantTemp(int coolantTemp) {
		itemArray[5] = String.valueOf(coolantTemp);
	}
	
	/**
	 * 第7项、
	 * 引擎负荷
	 * @param enginePayload
	 */
	public void addEnginePayload(int enginePayload) {
		itemArray[6] = String.valueOf(enginePayload);
	}
	
	/**
	 * 第8项
	 * 燃油百分比
	 * @param fuelPct
	 */
	public void addFuelPct(int fuelPct) {
		itemArray[7] = String.valueOf(fuelPct);
	}
	
	/**
	 * 第9项
	 * 燃油压力
	 * @param fuelPressure
	 */
	public void addFuelPressure(int fuelPressure) {
		itemArray[8] = String.valueOf(fuelPressure);
	}
	
	/**
	 * 第10项
	 * 进气歧管绝对压力
	 * @param imaPressure
	 */
	public void addIMAPressure(int imaPressure) {
		itemArray[9] = String.valueOf(imaPressure);
	}
	
	/**
	 * 第11项
	 * 进气温度
	 * @param intakeAirTemperature
	 */
	public void addIntakeAirTemperature(int intakeAirTemperature) {
		itemArray[10] = String.valueOf(intakeAirTemperature);
	}
	
	/**
	 * 第12项
	 * 进气流量
	 * @param imaf
	 */
	public void addIMAF(int imaf) {
		itemArray[11] = String.valueOf(imaf);
	}
	
	
	/**
	 * 第13项
	 * 节气门绝对位置
	 * @param throttlePos
	 */
	public void addThrottlePos(int throttlePos) {
		itemArray[12] = String.valueOf(throttlePos);
	}
	
	public byte[] writeToBytes() {
		
		String content = "";

		for(String s : itemArray) {
			if ( s != null ) {
				content += s;
			}
			content += ";";
		}

		content = content.substring(0, content.length() - 1);
		return content.getBytes();
		
	}

	public void readFromBytes(byte[] bytes) {
		
		MyBuffer buff = new MyBuffer(bytes);

		String content = buff.getString();
		
		String[] tmp = content.split(";");
		System.arraycopy(tmp, 0, itemArray, 0, tmp.length);
		
	}

	@Override
	public String toString() {
		String content = "";

		for(int i = 0; i < nArraySize; i++) {
			
			if ( itemArray[i] != null ) {
				content += itemArray[i];
			}
			content += ";";
		}
		
		content = content.substring(0, content.length() - 1);
		return content;
	}

	
	public static void main(String[] args) {
		
		PAI_Obd obdItems = new PAI_Obd();
		
		//obdItems.addRpm(3500);
		obdItems.addSpeed(82);
		obdItems.addVoltage(132);
		
		byte[] tmp = obdItems.writeToBytes();
		
		logger.info(Tools.ToHexFormatString(tmp));
		
		obdItems.readFromBytes(tmp);
		
		logger.info(obdItems.toString());
	}
}
