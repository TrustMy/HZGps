package com.sy.hzgps.protocol;


import com.sy.hzgps.tool.sy.ClassUtils;
import com.sy.hzgps.tool.sy.Tools;

public class SY808MessageFactory {
	public static IMessageBody Create(short messageType, byte[] messageBodyBytes) {

		String nameSpace = SY808MessageFactory.class.getPackage().getName();
		String className = nameSpace + ".SY_" + Tools.ToHexString((short)messageType).toUpperCase();

		Object messageBody = ClassUtils.getBean(className);
		if (messageBody != null) {
			IMessageBody msg = (IMessageBody) messageBody;
			msg.readFromBytes(messageBodyBytes);
			return msg;
		}
		return null;
	}
}
