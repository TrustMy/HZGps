package com.sy.hzgps.protocol;



public final class PositionAdditionalFactory {
	public static IAdditionalItem CreatePositionalFactory(byte additionalId, byte[] bytes) {
		IAdditionalItem additional = null;
		switch (additionalId) {
		case (byte) 0xE1:
			additional = new PAI_EngineStatus();
		    additional.readFromBytes(bytes);
		    break;
		case (byte) 0xE2:
			additional = new PAI_Obd();
			additional.readFromBytes(bytes);
			break;
		case (byte) 0xE3:
			additional = new PAI_Alarm();
			additional.readFromBytes(bytes);
			break;
		default:
			break;
		}
		return additional;
	}
}