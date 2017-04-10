package com.sy.hzgps.protocol;



import com.sy.hzgps.tool.sy.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SY808Message {

	public static final byte Prefix = 0x7e;
	public static final byte Escape_Prefix = 0x7d;

	public static byte[] childPacket;

	/**
	 *  消息头
	 */
	private SY808MessageHeader messageHeader = new SY808MessageHeader();

	public void setMessageHeader(SY808MessageHeader header) {
		messageHeader = header;
	}

	public final SY808MessageHeader getMessageHeader() {
		return messageHeader;
	}

	/**
	 *  消息体
	 */
	private IMessageBody messageBody = null;

	public final IMessageBody getMessageBody() {
		return messageBody;
	}

	public final void setMessageBody(IMessageBody value) {
		messageBody = value;
	}

	/**
	 *  消息头描述
	 */
	private String packetDescr;

	public final String getPacketDescr() {
		return packetDescr;
	}

	public final void setPacketDescr(String value) {
		packetDescr = value;
	}

	/**
	 *  字节流转换为消息
	 * @param messageBytes
	 */
	public void readFromBytes(byte[] messageBytes) {

		// 反转义
		byte[] validMessageBytes = unEscape(messageBytes);

		// 校验Xor
		byte xor = getCheckSumXor(validMessageBytes, 1, validMessageBytes.length - 2);
		byte realXor = validMessageBytes[validMessageBytes.length - 2];

		if (xor == realXor) {
			int tempLen = validMessageBytes.length - 3; // 去掉报文头0x7E，校验码，报文尾0x7E
			byte[] headerBytes = new byte[tempLen < 16 ? 12 : 16];
			System.arraycopy(validMessageBytes, 1, headerBytes, 0, headerBytes.length);
			messageHeader.readFromBytes(headerBytes);

			int start = 17;
			if (!messageHeader.getIsPackage()) { // 不分包则少4个字节的分包信息
				start = 13;
			}

			if (messageHeader.getMessageSize() > 0) {
				byte[] sourceData = new byte[messageHeader.getMessageSize()];
				System.arraycopy(validMessageBytes, start, sourceData, 0, sourceData.length);

				if (messageHeader.getIsPackage()) {
					// 分包的消息体是纯数据不进行解析，保留在消息中.

					childPacket = new byte[messageHeader.getMessageSize()];
					System.arraycopy(sourceData, 0, childPacket, 0, messageHeader.getMessageSize());
					getWholePacket();// 等所有的包都到达后，再进行解析

				} else {
					messageBody = SY808MessageFactory.Create(messageHeader.getMessageType(), sourceData);
				}
			}
		}
	}

	/**
	 *  将消息转换为字节流
	 * @return
	 */
	public final byte[] writeToBytes() {

		MyBuffer buff = new MyBuffer();

		byte[] bodyBytes = null;
		if (messageBody != null) {
			bodyBytes = messageBody.writeToBytes();
		}

		if (bodyBytes != null) {
			messageHeader.setMessageSize(bodyBytes.length);
			messageHeader.setIsPackage(false);
			byte[] headerBytes = messageHeader.writeToBytes();
			buff.putByteArray(headerBytes);
			buff.putByteArray(bodyBytes);
		} else {
			messageHeader.setMessageSize(0);
			byte[] headerBytes = messageHeader.writeToBytes();
			buff.putByteArray(headerBytes);
		}

		byte[] messageBytes = buff.array();
		byte checkSum = getCheckSumXor(messageBytes, 0, messageBytes.length);
		// messageBytes[messageBytes.length - 1] = checkSum; // 填充校验码
		buff.putByte(checkSum);
		byte[] escapedBytes = escape(buff.array()); // 转义
		buff.clear();
		buff.putByte(Prefix);
		buff.putByteArray(escapedBytes);
		buff.putByte(Prefix);

		byte[] data = buff.array();
		packetDescr = Tools.ToHexString(data);
		return data;

	}

	public static ConcurrentMap<String, List<byte[]>> msgMap = new ConcurrentHashMap<String, List<byte[]>>();

	private void getWholePacket() {

		if (messageHeader.getIsPackage() == false)
			return;

		String key = messageHeader.getPhoneNumber() + "_" + messageHeader.getMessageType();

		if (messageHeader.getMessagePacketSerialNo() == 1) {
			List<byte[]> ls = new ArrayList<byte[]>();
			ls.add(childPacket);
			msgMap.put(key, ls);
		} else if (messageHeader.getMessagePacketSerialNo() > 1) {
			List<byte[]> ls = msgMap.get(key);
			ls.add(childPacket);
			if (messageHeader.getMessagePacketSerialNo() == messageHeader.getMessagePacketTotalCount()) {
				int totalByteNum = 0;
				for (byte[] bs : ls) {
					totalByteNum += bs.length;
				}
				byte[] totalBytes = new byte[totalByteNum];
				int start = 0;
				for (byte[] bs : ls) {
					System.arraycopy(bs, 0, totalBytes, start, bs.length);
					start += bs.length;
				}
				messageBody = SY808MessageFactory.Create(messageHeader.getMessageType(), totalBytes);
				msgMap.remove(key);
			}
		}
	}

	/**
	 *  反转义
	 * @param data
	 * @return
	 */
	private byte[] unEscape(byte[] data) {
		MyBuffer buff = new MyBuffer();
		for (int i = 0; i < data.length; i++) {
			if (data[i] == Escape_Prefix) {
				if (data[i + 1] == 0x01) {
					buff.putByte((byte) Escape_Prefix);
					i++;
				} else if (data[i + 1] == 0x02) {
					buff.putByte((byte) Prefix);
					i++;
				}
			} else {
				buff.putByte(data[i]);
			}
		}

		return buff.array();
	}

	/**
	 *  转义
	 * @param data
	 * @return
	 */
	private byte[] escape(byte[] data) {
		MyBuffer tmp = new MyBuffer();
		for (int j = 0; j < data.length; j++) {
			if (data[j] == Escape_Prefix) {
				tmp.putByte((byte) Escape_Prefix);
				tmp.putByte((byte) 0x01);
			} else if (data[j] == Prefix) {
				tmp.putByte((byte) Escape_Prefix);
				tmp.putByte((byte) 0x02);
			} else {
				tmp.putByte(data[j]);
			}
		}

		return tmp.array();
	}

	/**
	 * 异或校验
	 * @param data
	 * @param startPos
	 * @param len
	 * @return
	 */
	private byte getCheckSumXor(byte[] data, int startPos, int len) {
		byte checkSum = 0;
		for (int i = startPos; i < len; i++) {
			checkSum ^= data[i];
		}
		return checkSum;
	}
	
	@Override
	public String toString() {
		String s = String.format("命令ID: 0x%04X, 命令序列号: 0x%04X, 命令内容: %s",
				messageHeader.getMessageType(), messageHeader.getMessageSerialNo(), messageBody.toString());
		
		return s;
	}
}
