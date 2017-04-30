package com.sy.hzgps.network.ca;




import com.sy.hzgps.ApkConfig;
import com.sy.hzgps.MyContext;
import com.sy.hzgps.database.DBManagerLH;
import com.sy.hzgps.message.GpsMessage;
import com.sy.hzgps.message.ObdMessage;
import com.sy.hzgps.message.ObdMessageID;
import com.sy.hzgps.message.ca.CAAlarmReportMessage;
import com.sy.hzgps.protocol.PAI_Alarm;
import com.sy.hzgps.protocol.PAI_EngineStatus;
import com.sy.hzgps.protocol.SY808Message;
import com.sy.hzgps.protocol.SY808MessageHeader;
import com.sy.hzgps.protocol.SY_0102;
import com.sy.hzgps.protocol.SY_01AA;
import com.sy.hzgps.protocol.SY_0200;
import com.sy.hzgps.tool.lh.L;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Date;

public class CAEncoder extends ProtocolEncoderAdapter {
	private DBManagerLH dbManagerLH;
	private final Charset charset;
	private CACommHelper commHelper;

	private static final Logger logger = LoggerFactory.getLogger(CAEncoder.class);

	public CAEncoder(CACommHelper commHelper) {
		this(Charset.defaultCharset(), commHelper);
	}

	public CAEncoder(Charset charset, CACommHelper commHelper) {
		this.charset = charset;
		this.commHelper = commHelper;
		dbManagerLH = new DBManagerLH(MyContext.getContext());
	}

    //在此处实现对Msg ProtocolEncoder包的编码工作，并把它写入输出流中
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        //logger.info("--- Msg ProtocolEncoder encode() ---  ");
		if (message != null && message instanceof ObdMessage) {
			L.d("message:"+message.toString()+"|id:"+((ObdMessage) message).getMessageId());
			ObdMessage obdMessage = (ObdMessage) message;

			SY808Message sendMessage = null;
			switch(obdMessage.getMessageId()) {
				case REGISTER:
					sendMessage = process_01AA(obdMessage);
					break;

				case AUTHENTICATE:
					sendMessage = process_0102(obdMessage);
					break;

				case HEARTBEAT:
					sendMessage = process_0002(obdMessage);
					break;

				case OBD_REPORT:
				case OVER_SPEED_ALARM:
					sendMessage = process_0200(obdMessage);
					break;

				default:
					logger.info("UnSupported message!");
					break;
			}

			if ( sendMessage != null ) {

				byte[] value = sendMessage.writeToBytes();
				IoBuffer buf = IoBuffer.allocate(value.length).setAutoExpand(true);
				buf.put(value);

				buf.flip();
				out.write(buf);


				String s = bytesToHexString(value);
				L.d("data :"+s);


				/*
                // TEST
                if ( sendMessage.getMessageHeader().getMessageType() == 0x0200 ) {
                    short messageSerialNo = sendMessage.getMessageHeader().getMessageSerialNo();
                    String messageDescription = Tools.ToHexFormatString(value);
                    String desc = String.format("Serial 0x%04x", messageSerialNo) + " " + messageDescription;
                    logger.info(desc);

                }
               // */


			} else {
				logger.info("HXMessage is not valid, will not write any byte");
			}
		}

	}


	public  final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

    public void dispose() throws Exception {
		// Do nothing
	}


	private SY808Message process_01AA(ObdMessage obdMessage) {

		SY808Message message = new SY808Message();

		SY808MessageHeader header = message.getMessageHeader();

		header.setMessageType((short) 0x01AA);
		header.setMessageSerialNo(obdMessage.getSerialNo());
		header.setPhoneNumber(commHelper.getTerminalId());
		header.setIsPackage(false);

		SY_01AA body = new SY_01AA();

		body.setTerminalId("00"+commHelper.getTerminalId());
		body.setLicense(commHelper.getLicense());
		message.setMessageBody(body);

		return message;

	}

	private SY808Message process_0102(ObdMessage obdMessage) {

		SY808Message message = new SY808Message();

		SY808MessageHeader header = message.getMessageHeader();

		header.setMessageType((short) 0x0102);
		header.setMessageSerialNo(obdMessage.getSerialNo());
		header.setPhoneNumber(commHelper.getTerminalId());
		header.setIsPackage(false);

		SY_0102 body = new SY_0102();

		body.setRegisterNo(commHelper.getAuthCode());
		message.setMessageBody(body);

		return message;
	}

	private SY808Message process_0002(ObdMessage obdMessage) {
		SY808Message message = new SY808Message();

		SY808MessageHeader header = message.getMessageHeader();

		header.setMessageType((short) 0x0002);
		header.setMessageSerialNo(obdMessage.getSerialNo());
		header.setPhoneNumber(commHelper.getTerminalId());
		header.setIsPackage(false);

		return message;
	}

	private SY808Message process_0200(ObdMessage obdMessage) {

		GpsMessage gpsMessage = (GpsMessage) obdMessage;


		SY808Message message = new SY808Message();
		SY808MessageHeader header = message.getMessageHeader();

		header.setMessageType((short) 0x0200);
		header.setMessageSerialNo(obdMessage.getSerialNo());
		header.setPhoneNumber(commHelper.getTerminalId());//commHelper.getSimNumber());
		header.setIsPackage(false);

		Date date;
		SY_0200 body = new SY_0200();

		if(ApkConfig.Time == 0){
			date = new Date();
		}else{
			date = new Date(ApkConfig.Time);
		}


		L.d("Year:"+(date.getYear()));
		body.setYear((byte)(date.getYear()-100));
		body.setMonth((byte)(date.getMonth()+1));
		body.setDate((byte)date.getDate());
		body.setHour((byte)date.getHours());
		body.setMinute((byte)date.getMinutes());
		body.setSecond((byte)date.getSeconds());

		body.setAltitude((short)(gpsMessage.getAlt()));
		body.setCourse((short)(gpsMessage.getBearing()));
		body.setLatitude((int)(gpsMessage.getLat()*1000000));
		body.setLongitude((int)(gpsMessage.getLng()*1000000));
		body.setSpeed((short)(gpsMessage.getGpsSpeed()*10));

		body.setFixed(gpsMessage.getFixed());

		PAI_EngineStatus engineStatus = new PAI_EngineStatus();
		engineStatus.setEngineStatus((byte)gpsMessage.getEngineStatus());

		body.addAdditional(engineStatus);

		dbManagerLH.addGps(gpsMessage.getLat(),gpsMessage.getLng(),gpsMessage.getGpsTime(),gpsMessage.getEngineStatus());

		if ( engineStatus.getEngineStatus() == 1 || engineStatus.getEngineStatus() == 4 ) {
			body.setAccOn(true);
		} else {
			body.setAccOn(false);
		}

		// 判断是否为报警

		if ( obdMessage.getMessageId() == ObdMessageID.OVER_SPEED_ALARM ) {
			CAAlarmReportMessage alarmReportMessage= (CAAlarmReportMessage) obdMessage;

			PAI_Alarm alarmInfo = new PAI_Alarm();
			alarmInfo.addOverSpeedAlarm(1, alarmReportMessage.getGpsSpeed());

			body.addAdditional(alarmInfo);


		}

		message.setMessageBody(body);

		return message;
	}


}
