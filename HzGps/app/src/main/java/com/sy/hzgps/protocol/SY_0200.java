package com.sy.hzgps.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * 位置汇报
 * 
 * @author jiayang
 *
 */
public class SY_0200 implements IMessageBody {

	private static final Logger logger = LoggerFactory.getLogger(SY_0200.class);

	// private static final String ENDING = "\r";

	/**
	 * 报警标志
	 */
	private int alarmFlag;

	public void setAlarmFlag(int flag) {
		alarmFlag = flag;
	}

	public int getAlarmFlag() {
		return alarmFlag;
	}

	/**
	 * 状态位
	 */
	private int status;

	public void setStatus(int v) {
		status = v;
	}

	public int getStatus() {
		return status;
	}

	/**
	 * 纬度，以度为单位的纬度值乘以10的6次方，精确到百万 分之一度
	 */
	private int latitude;

	public final int getLatitude() {
		return latitude;
	}

	public void setLatitude(int value) {
		latitude = value;
	}

	/**
	 * 经度，以度为单位的经度值乘以10的6次方，精确到百万 分之一度
	 */
	private int longitude;

	public final int getLongitude() {
		return longitude;
	}

	public void setLongitude(int value) {
		longitude = value;
	}

	/**
	 * 海拔高度，单位为米（m）
	 */
	private short altitude;

	public final short getAltitude() {
		return altitude;
	}

	public void setAltitude(short value) {
		altitude = value;
	}

	/**
	 * 速度，单位：0.1km/h
	 */
	private short speed;

	public final short getSpeed() {
		return speed;
	}

	public void setSpeed(short value) {
		speed = value;
	}

	/**
	 * 方向,0～359，正北为0，顺时针
	 */
	private short course;

	public final short getCourse() {
		return course;
	}

	public void setCourse(short value) {
		course = value;
	}

	/**
	 * 年
	 */
	private byte year;

	public void setYear(byte v) {
		year = v;
	}

	public final byte getYear() {
		return year;
	}

	/**
	 * 月
	 */
	private byte month;

	public void setMonth(byte v) {
		month = v;
	}

	public final byte getMonth() {
		return month;
	}

	/**
	 * 日
	 */
	private byte date;

	public void setDate(byte v) {
		date = v;
	}

	public final byte getDate() {
		return date;
	}

	/**
	 * 时
	 */
	private byte hour;

	public void setHour(byte v) {
		hour = v;
	}

	public final byte getHour() {
		return hour;
	}

	/**
	 * 分
	 */
	private byte minute;

	public void setMinute(byte v) {
		minute = v;
	}

	public final byte getMinute() {
		return minute;
	}

	/**
	 * 秒
	 */
	private byte second;

	public void setSecond(byte v) {
		second = v;
	}

	public final byte getSecond() {
		return second;
	}

	/**
	 * 是否定位
	 */
	private boolean fixed;

	public void setFixed(boolean fixed) {
		this.fixed = fixed;

		if (fixed) {
			status = status | 0x00000002;
		} else {
			status = status & 0xFFFFFFD;
		}
	}

	public boolean getFixed() {
		return fixed;
	}

	/**
	 * 是否引擎启动
	 */
	private boolean accOn;

	public void setAccOn(boolean accOn) {
		this.accOn = accOn;

		if (accOn) {
			status = status | 0x00000001;
		} else {
			status = status & 0xFFFFFFFE;
		}
	}

	public boolean getAccOn() {
		return accOn;
	}

	private ArrayList<IAdditionalItem> additionals = null;

	public ArrayList<IAdditionalItem> getAdditionals() {
		return additionals;
	}

	public void setAdditionals(ArrayList<IAdditionalItem> v) {
		additionals = v;
	}

	public void addAdditional(IAdditionalItem v) {
		if (additionals == null) {
			additionals = new ArrayList<IAdditionalItem>();
		}

		additionals.add(v);
	}

	public byte[] writeToBytes() {
		MyBuffer buff = new MyBuffer();

		buff.putInt(alarmFlag);
		buff.putInt(status);
		buff.putInt(latitude);
		buff.putInt(longitude);
		buff.putShort(altitude);
		buff.putShort(speed);
		buff.putShort(course);
		buff.putByte(Byte.parseByte(String.valueOf(year), 16));
		buff.putByte(Byte.parseByte(String.valueOf(month), 16));
		buff.putByte(Byte.parseByte(String.valueOf(date), 16));
		buff.putByte(Byte.parseByte(String.valueOf(hour), 16));
		buff.putByte(Byte.parseByte(String.valueOf(minute), 16));
		buff.putByte(Byte.parseByte(String.valueOf(second), 16));

		if (additionals != null && additionals.size() > 0) {
			for (IAdditionalItem ad : additionals) {
				buff.putByte(ad.getAdditionalId());
				buff.putByte(ad.getAdditionalLength());
				buff.putByteArray(ad.writeToBytes());
			}
		}

		return buff.array();
	}

	public void readFromBytes(byte[] messageBodyBytes) {
		MyBuffer buff = new MyBuffer(messageBodyBytes);

		setAlarmFlag(buff.getInt());
		setStatus(buff.getInt());

		if ((getStatus() & 0x00000002) == 0x00000002) {
			setFixed(true);
		} else {
			setFixed(false);
		}

		if ((getStatus() & 0x00000001) == 0x00000001) {
			setAccOn(true);
		} else {
			setAccOn(false);
		}

		setLatitude(buff.getInt());
		setLongitude(buff.getInt());
		setAltitude(buff.getShort());
		setSpeed(buff.getShort());
		setCourse(buff.getShort());

		setYear(Byte.parseByte(String.format("%02X", buff.get())));
		setMonth(Byte.parseByte(String.format("%02X", buff.get())));
		setDate(Byte.parseByte(String.format("%02X", buff.get())));
		setHour(Byte.parseByte(String.format("%02X", buff.get())));
		setMinute(Byte.parseByte(String.format("%02X", buff.get())));
		setSecond(Byte.parseByte(String.format("%02X", buff.get())));

		setAdditionals(new ArrayList<IAdditionalItem>());
		int pos = 28;
		while (buff.hasRemain()) {
			byte additionalId = buff.get();
			int additionalLength = (int) (0x000000FF & buff.get());
			byte[] additionalBytes = buff.gets(additionalLength);
			IAdditionalItem item = PositionAdditionalFactory.CreatePositionalFactory(additionalId, additionalBytes);
			if (item != null) {
				additionals.add(item);
			} else {
				logger.error("未知的附加协议:" + additionalId + ",附加长度:" + additionalLength);
			}
			pos = pos + 2 + additionalLength;
		}

	}
	/*
	@Override
	public String toString() {

		String alarmString = toAlarmInfoString();
		String obdString = toObdInfoString();

		if (alarmString != null) {

			return alarmString;
			
		} else {

			return obdString;

		}
	}
*/
	

	public String toObdInfoString() {

		String obdInfo = null;
		String engineInfo = null;
		for (IAdditionalItem ad : additionals) {

			byte id = ad.getAdditionalId();
			if (id == (byte) 0xE2) {
				obdInfo = ad.toString();
				break;
			} else if (id == (byte) 0xE1) {
				engineInfo = ad.toString();
			}
		}

		if (obdInfo == null) {
			obdInfo = ";;;;;;;;;;;;";
		}
		
		String timestamp = String.format("%04d%02d%02d%02d%02d%02d;", getYear() + 2000, getMonth(), getDate(),
				getHour(), getMinute(), getSecond());
		String fixed = String.format("%d;", getFixed() ? 1 : 0);

		int latNumber = getLatitude();
		String lat;

		if (latNumber != 0) {
			String tmp = String.valueOf(latNumber);
			lat = tmp.substring(0, tmp.length() - 6) + "." + tmp.substring(tmp.length() - 6) + ";";
		} else {
			lat = "0;";
		}

		int lngNumber = getLongitude();
		String lng;

		if (lngNumber != 0) {
			String tmp = String.valueOf(lngNumber);
			lng = tmp.substring(0, tmp.length() - 6) + "." + tmp.substring(tmp.length() - 6) + ";";
		} else {
			lng = "0;";
		}

		String altitude = String.format("%d;", getAltitude());
		String gpsSpeed = String.format("%.1f;", (float) getSpeed() / 10.0f);
		String course = String.format("%d;", getCourse());

		return timestamp + fixed + lat + lng + altitude + gpsSpeed + course + engineInfo + ";" + obdInfo;

	}

	public String toAlarmInfoString() {

		String alarmInfo = null;
		String engineInfo = null;
		for (IAdditionalItem ad : additionals) {

			byte id = ad.getAdditionalId();

			if (id == (byte) 0xE3) {
				alarmInfo = ad.toString();
			} else if (id == (byte) 0xE1) {
				engineInfo = ad.toString();
			}
		}

		if (alarmInfo != null) {
			String timestamp = String.format("%04d%02d%02d%02d%02d%02d,", getYear() + 2000, getMonth(), getDate(),
					getHour(), getMinute(), getSecond());

			int latNumber = getLatitude();
			String lat;

			if (latNumber != 0) {
				String tmp = String.valueOf(latNumber);
				lat = tmp.substring(0, tmp.length() - 6) + "." + tmp.substring(tmp.length() - 6) + ",";
			} else {
				lat = "0,";
			}

			int lngNumber = getLongitude();
			String lng;

			if (lngNumber != 0) {
				String tmp = String.valueOf(lngNumber);
				lng = tmp.substring(0, tmp.length() - 6) + "." + tmp.substring(tmp.length() - 6) + ";";
			} else {
				lng = "0;";
			}

			return timestamp + lat + lng + engineInfo + ";" + alarmInfo;
		} else {
			return null;
		}

	}

}
