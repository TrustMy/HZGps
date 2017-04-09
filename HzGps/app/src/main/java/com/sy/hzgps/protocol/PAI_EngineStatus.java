package com.sy.hzgps.protocol;

public class PAI_EngineStatus implements IAdditionalItem {

	/**
	 * 引擎状态：
	 * 		0:	已熄火，处于静止状态
	 * 		1：	点火
	 * 		2：	熄火
	 * 		4：	已点火，处于行驶状态
	 */
	private byte engineStatus = 0;
	
	public void setEngineStatus(byte status) {
		this.engineStatus = status;
	}
	
	public byte getEngineStatus() {
		return engineStatus;
	}
	
	public byte getAdditionalId() {
		return (byte)0xE1;
	}

	public byte getAdditionalLength() {
		return 1;
	}

	public byte[] writeToBytes() {
		byte[] bytes = new byte[1];
		bytes[0] = engineStatus;
		return bytes;
	}

	public void readFromBytes(byte[] bytes) {
		engineStatus = bytes[0];		
	}

	@Override
	public String toString() {
		return String.format("%d", engineStatus);
	}
}
