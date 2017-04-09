package com.sy.hzgps.protocol;

public interface IAdditionalItem {
	
	public byte getAdditionalId();

	public byte getAdditionalLength();

	public byte[] writeToBytes();

	public void readFromBytes(byte[] bytes);
}