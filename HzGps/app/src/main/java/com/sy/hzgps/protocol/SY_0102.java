package com.sy.hzgps.protocol;

/**
 *  终端鉴权
 * @author jiayang
 *
 */
public class SY_0102 implements IMessageBody {
	
	/**
	 * 鉴权码
	 */
	private String registerNo;

	public final String getRegisterNo() {
		return registerNo;
	}

	public final void setRegisterNo(String value) {
		registerNo = value;
	}

	public byte[] writeToBytes() {
		return getRegisterNo().getBytes();
	}

	public void readFromBytes(byte[] bytes) {
		setRegisterNo(new String(bytes));
	}

	@Override
	public String toString() {
		return String.format("鉴权码：%1$s", getRegisterNo());
	}
	
}