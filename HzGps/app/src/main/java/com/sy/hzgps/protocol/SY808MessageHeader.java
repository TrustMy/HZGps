package com.sy.hzgps.protocol;



import com.sy.hzgps.tool.sy.Tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SY808MessageHeader {
	
	public static final Logger logger = LoggerFactory.getLogger(SY808MessageHeader.class);
	
	/**
	 * 消息ID
	 */
	private short messageType = 0;

	public void setMessageType(short messageType) {
		this.messageType = messageType;
	}

	public final short getMessageType() {
		return messageType;
	}

	/**
	 * 消息属性
	 */
	private short messageProperty = 0;

	public void setMessageProperty(short messageProperty) {
		this.messageProperty = messageProperty;
	}

	public final short getMessageProperty() {
		return messageProperty;
	}

	/**
	 * 终端手机号
	 */
	private String phoneNumber = "";

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public final String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * 消息流水号
	 */
	private short messageSerialNo = 0;

	public void setMessageSerialNo(short messageSerialNo) {
		this.messageSerialNo = messageSerialNo;
	}

	public final short getMessageSerialNo() {
		return messageSerialNo;
	}

	/**
	 * 总分包数
	 */
	private short messagePacketTotalCount = 0;

	public void setMessagePacketTotalCount(short messagePacketTotalCount) {
		this.messagePacketTotalCount =  messagePacketTotalCount;
	}

	public final int getMessagePacketTotalCount() {
		return messagePacketTotalCount;
	}

	/**
	 * 分包编号
	 */
	private short messagePacketSerialNo = 0;

	public void setMessagePacketSerialNo(short messagePacketSerialNo) {
		this.messagePacketSerialNo = messagePacketSerialNo;
	}

	public final short getMessagePacketSerialNo() {
		return messagePacketSerialNo;
	}
	
	public SY808MessageHeader() {
	
	}
	
	/**
	 *  字节流转化为消息头
	 * @param headerBytes
	 */
	public void readFromBytes(byte[] headerBytes) {
		
		if ( headerBytes != null && headerBytes.length >= 12 ) {
			MyBuffer buff = new MyBuffer(headerBytes);
			setMessageType(buff.getShort());
			setMessageProperty(buff.getShort());
			byte[] simIdBytes = buff.gets(6);
			String simId = (String.format("%02X", simIdBytes[0]) + String.format("%02X", simIdBytes[1])
					+ String.format("%02X", simIdBytes[2]) + String.format("%02X", simIdBytes[3])
					+ String.format("%02X", simIdBytes[4]) + String.format("%02X", simIdBytes[5]));
			setPhoneNumber(simId);//simId.substring(1, simId.length()));
			setMessageSerialNo(buff.getShort());
			if (getIsPackage()) {
				setMessagePacketTotalCount(buff.getShort());
				setMessagePacketSerialNo(buff.getShort());
			}
		}
		
	}
	
	/**
	 *  消息头转化为字节流
	 * @return
	 */
	public final byte[] writeToBytes() {
		
		MyBuffer buff = new MyBuffer();
		
		buff.putShort(messageType);
		buff.putShort(messageProperty);
		while (phoneNumber.length() < 12) {
			phoneNumber = "0" + phoneNumber;
		}
		byte[] simIdBytes = Tools.HexString2Bytes(phoneNumber);
		buff.putByteArray(simIdBytes);
		buff.putShort(messageSerialNo);
		if (getIsPackage()) {
			buff.putShort(messagePacketTotalCount);
			buff.putShort(messagePacketSerialNo);
		}

		return buff.array();
	}
	

	/**
	 * 设置分包类型
	 * @param value
	 */
	public void setIsPackage(boolean value) {
		if ( value ) {
			messageProperty |= 0x2000;
		} else {
			messageProperty &= 0xDFFF;
		}
	}
	
	public final boolean getIsPackage() {
		return  ( messageProperty & 0x2000) == 0x2000;
	}
	
	/**
	 * 设置消息体长度
	 * @param value
	 */
	public void setMessageSize(int value) {
		if ( getIsPackage() ) {
			messageProperty = (short) (0x2000 | value);
		} else {
			messageProperty = (short) (value);
		}
	}
	
	/**
	 * 返回消息头长度
	 * @return
	 */
	public final int getMessageSize() {
		return messageProperty & 0x03FF;
	}
	
	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(String.format("手机：%s, 消息体长度：%d, 是否分包：%b，总分包数目：%d，分包编号：%d，消息流水号：0x%04x，消息ID: 0x%04x",
				getPhoneNumber(), getMessageSize(), getIsPackage(), getMessagePacketTotalCount(), getMessagePacketSerialNo(), getMessageSerialNo(), getMessageType()));
		return sBuilder.toString();
	}
	
	public static void main(String[] args) {
		
		SY808MessageHeader header = new SY808MessageHeader();
		
		header.setMessageType((short)0x0100);
		header.setMessageSerialNo((short)0x0010);
		header.setPhoneNumber("13800000001");
		header.setIsPackage(false);
		header.setMessageSize(45);
		
		byte[] bytes = header.writeToBytes();
		
		logger.info(Tools.ToHexString(bytes));
		
		header.readFromBytes(bytes);
		
		logger.info(header.toString());
	}
}
