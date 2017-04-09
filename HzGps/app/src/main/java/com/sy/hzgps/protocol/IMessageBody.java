package com.sy.hzgps.protocol;

public interface IMessageBody {
	
	public byte[] writeToBytes();
	
	public void readFromBytes(byte[] messageBodyBytes);
}