package com.sy.hzgps.protocol;


/**
 * 圣禹私有协议---终端注册应答
 */
public class SY_81AA implements IMessageBody {
	/**
	 *  对应的终端注册消息的流水号
	 */
	private short registerResponseMessageSerialNo;

	public final short getRegisterResponseMessageSerialNo() {
		return registerResponseMessageSerialNo;
	}

	public final void setRegisterResponseMessageSerialNo(short value) {
		registerResponseMessageSerialNo = value;
	}

	/**
	 *  注册结果
	 *  0：成功；1：车辆已被注册；2：数据库中无该车辆；3：终端已被注册；4：数据库中无该终端
	 */
	private byte registerResponseResult;

	public final byte getRegisterResponseResult() {
		return registerResponseResult;
	}

	public final void setRegisterResponseResult(byte value) {
		registerResponseResult = value;
	}

	/**
	 *  主通信服务器地址
	 */
	private String commHost;

	public final String getCommHost() {
		return commHost;
	}

	public final void setCommHost(String value) {
		commHost = value;
	}
	
	/**
	 * 主通信服务器端口
	 */
	private int commPort;
	
	public final int getCommPort() {
		return commPort;
	}
	
	public final void setCommPort(int port) {
		commPort = port;
	}
	
	/**
	 *  备通信服务器地址
	 */
	private String comm2Host;

	public final String getComm2Host() {
		return comm2Host;
	}

	public final void setComm2Host(String value) {
		comm2Host = value;
	}
	
	/**
	 * 备通信服务器端口
	 */
	private int comm2Port;
	
	public final int getComm2Port() {
		return comm2Port;
	}
	
	public final void setComm2Port(int port) {
		comm2Port = port;
	}
	
	/**
	 * 鉴权码
	 */
	private String authCode;
	
	public final String getAuthCode() {
		return authCode;
	}
	
	public final void setAuthCode(String code) {
		authCode = code;
	}

	public byte[] writeToBytes() {

		MyBuffer buff = new MyBuffer();

		buff.putShort(getRegisterResponseMessageSerialNo());
		buff.putByte(getRegisterResponseResult());
		if (getRegisterResponseResult() == 0 || getRegisterResponseResult() == 1 || getRegisterResponseResult() == 3 ) {
			String str = String.format("%s:%d;%s:%d;%s", commHost, commPort, comm2Host, comm2Port, authCode);
			buff.putString(str);
		}
		return buff.array();
	}

	public void readFromBytes(byte[] bytes) {

		MyBuffer buff = new MyBuffer(bytes);

		setRegisterResponseMessageSerialNo(buff.getShort());
		setRegisterResponseResult(buff.get());
		if (getRegisterResponseResult() == 0 || getRegisterResponseResult() == 1 || getRegisterResponseResult() == 3 ) {
			String str = buff.getString();
			String[] strArray = str.split(";");
			
			String[] commArray = strArray[0].split(":");
			setCommHost(commArray[0]);
			setCommPort(Integer.parseInt(commArray[1]));
			
			String[] comm2Array = strArray[1].split(":");
			setComm2Host(comm2Array[0]);
			setComm2Port(Integer.parseInt(comm2Array[1]));
			
			setAuthCode(strArray[2]);
		}
	}
	
	@Override
	public String toString() {
		
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(String.format("圣禹终端注册应答： 应答消息ID：0x%04x， 应答结果：%d， 主通信服务器地址：%s, "
				+ "主通信服务器端口：%d, 备通信服务器地址：%s, 备服务器端口：%d, 鉴权码：%s", 
				getRegisterResponseMessageSerialNo(), getRegisterResponseResult(), getCommHost(), getCommPort(), getComm2Host(), getComm2Port(), getAuthCode()));
		return sBuilder.toString();
		
	}
}