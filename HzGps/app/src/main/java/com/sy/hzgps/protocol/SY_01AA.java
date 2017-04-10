package com.sy.hzgps.protocol;



import com.sy.hzgps.tool.sy.Tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 圣禹私有协议--终端注册
 */
public class SY_01AA implements IMessageBody {
	
	public static final Logger logger = LoggerFactory.getLogger(SY_01AA.class);

	/**
	 * 车牌号码
	 */
	private String license;

	public final String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}
	
	/**
	 *  终端ID
	 */
	private String terminalId;

	public final String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String value) {
		terminalId = value;
	}

	
	public byte[] writeToBytes() {
		MyBuffer buff = new MyBuffer();

		byte[] idBytes = Tools.HexString2Bytes(terminalId);
		
		buff.putByteArray(idBytes);
		buff.putByteArray(license.getBytes());


		return buff.array();
	}

	public void readFromBytes(byte[] bytes) {
		
		//logger.info("Reg Hex: " + Tools.ToHexFormatString(bytes));
		String termId = (String.format("%02X", bytes[0])
				+ String.format("%02X", bytes[1]) + String.format("%02X", bytes[2])
				+ String.format("%02X", bytes[3]) + String.format("%02X", bytes[4])
				+ String.format("%02X", bytes[5]) + String.format("%02X", bytes[6]));
		setTerminalId(termId);

		byte[] licenseBytes = new byte[bytes.length - 7];
		System.arraycopy(bytes, 7, licenseBytes, 0, bytes.length - 7);

		setLicense(new String(licenseBytes));
	}
	
	@Override
	public String toString() {
		String s = "终端ID: " + terminalId + " 车牌号: " + license;
		return s;
	}

}