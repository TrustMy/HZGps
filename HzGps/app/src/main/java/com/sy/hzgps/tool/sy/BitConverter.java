package com.sy.hzgps.tool.sy;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BitConverter {

	//public static boolean IsLittleEndian = false;

	public static byte[] getBytes(byte num) {
		byte[] result = new byte[1];
		result[0] = num;
		return result;
	}
	
	public static byte[] getBytes(short num) {
		byte[] result = new byte[2];
		result[0] = (byte) (num >>> 8);
		result[1] = (byte) (num);
		return result;
	}
	
	public static byte[] getBytes(int num) {
		byte[] result = new byte[4];
		result[0] = (byte) (num >>> 24);
		result[1] = (byte) (num >>> 16);
		result[2] = (byte) (num >>> 8);
		result[3] = (byte) (num);
		return result;
	}
	
	public static byte[] getBytes(long num) {
		byte[] result = new byte[8];
		result[0] = (byte) (num >>> 56);
		result[1] = (byte) (num >>> 48);
		result[2] = (byte) (num >>> 40);
		result[3] = (byte) (num >>> 32);
		result[4] = (byte) (num >>> 24);
		result[5] = (byte) (num >>> 16);
		result[6] = (byte) (num >>> 8);
		result[7] = (byte) (num);
		return result;
	}

	public static short toUInt16(byte[] bytes, int start) {
		short value = 0;
		int m = 2;

		for (int i = 0; i < m; i++) {
			int shift = (m - 1 - i) * 8;
			value += (bytes[start + i] & 0x000000FF) << shift;
		}
		return value;
	}

	public static int toUInt32(byte[] bytes, int start) {
		int value = 0;
		int m = 4;

		for (int i = 0; i < m; i++) {
			int shift = (m - 1 - i) * 8;
			value += (bytes[start + i] & 0x000000FF) << shift;
		}
		return value;
	}

	public static int toUInt32(byte b) {
		int value = 0;
		int m = 4;

		int shift = (m - 1 - 3) * 8;
		value += (b & 0x000000FF) << shift;
		return value;
	}

	public static byte[] getBytes(String str) {
		try {
			return str.getBytes("gbk");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	public static Date getDate(byte[] bytes, int start) {
		StringBuilder sb = new StringBuilder();
		sb.append("20").append(String.format("%02X", bytes[start + 0])).append("-")
				.append(String.format("%02X", bytes[start + 1])).append("-")
				.append(String.format("%02X", bytes[start + 2])).append(" ")
				.append(String.format("%02X", bytes[start + 3])).append(":")
				.append(String.format("%02X", bytes[start + 4])).append(":")
				.append(String.format("%02X", bytes[start + 5]));
		String strDate = sb.toString();
		Date d = DateUtil.stringToDatetime(strDate, "yyyy-MM-dd HH:mm:ss");
		return d;
	}

	public static String getString(byte[] data) {
		return getString(data, 0, data.length);
	}

	public static String getString(byte[] data, int start, int len) {
		try {
			return new String(data, start, len, "gbk");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] getBytes(Date date) {
		int start = 0;
		byte[] bytes = new byte[6];
		bytes[start++] = Byte.parseByte(
				((date.getYear() + 1900) + "").substring(2, 4), 16);
		bytes[start++] = Byte.parseByte(((date.getMonth() + 1) + ""), 16);
		bytes[start++] = Byte.parseByte((date.getDay() + ""), 16);
		bytes[start++] = Byte.parseByte((date.getHours() + ""), 16);
		bytes[start++] = Byte.parseByte((date.getMinutes() + ""), 16);
		bytes[start++] = Byte.parseByte((date.getSeconds() + ""), 16);
		return bytes;
	}

	public static String format(Date date) {

		SimpleDateFormat time = new SimpleDateFormat("yy-MM-dd HH:mm:s");
		return time.format(date);
	}

	public int getUnsignedByte(byte data) {
		return data & 0x0FF;
	}

	public int getUnsignedByte(short data) {
		return data & 0x0FFFF;
	}

	public long getUnsignedIntt(int data) {
		return data & 0x0FFFFFFFFl;
	}

	public static void main(String[] args) {
		byte b = (byte) 130;
		int x = toUInt32(b);
		System.out.println(x);

		System.out.println((byte) 254);
	}

}
