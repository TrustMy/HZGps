package com.sy.hzgps.protocol;


import com.sy.hzgps.tool.BitConverter;

/**
 *  平台通用应答
 * @author jiayang
 *
 */
public class SY_8001 implements IMessageBody {

	/**
	 * 应答消息流水号
	 */
	private short responseMessageSerialNo;

	public final short getResponseMessageSerialNo() {
		return responseMessageSerialNo;
	}

	public final void setResponseMessageSerialNo(short value) {
		responseMessageSerialNo = value;
	}

	/**
	 *  应答消息ID
	 */
	private short responseMessageId;

	public final short getResponseMessageId() {
		return responseMessageId;
	}

	public final void setResponseMessageId(short value) {
		responseMessageId = value;
	}

	/**
	 *  应答结果
	 *  0：成功/确认；1：失败；2：消息有误；3：不支持；4：报警处理确认；
	 */
	private byte responseResult;

	public final byte getResponseResult() {
		return responseResult;
	}

	public final void setResponseResult(byte value) {
		responseResult = value;
	}

	public byte[] writeToBytes() {
		MyBuffer buff = new MyBuffer();

		buff.putShort(getResponseMessageSerialNo());
		buff.putShort(getResponseMessageId());
		buff.putByte(getResponseResult());

		return buff.array();

	}


	public void readFromBytes(byte[] messageBodyBytes) {

		setResponseMessageSerialNo((short) BitConverter.toUInt16(messageBodyBytes, 0));
		setResponseMessageId((short) BitConverter.toUInt16(messageBodyBytes, 2));
		setResponseResult(messageBodyBytes[4]);
	}
	
	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(String.format("平台应答： 应答消息流水号：0x%04x, 应答消息ID：0x%04x， 应答结果：%d",
				getResponseMessageSerialNo(), getResponseMessageId(), getResponseResult()));
		return sBuilder.toString();
	}
}