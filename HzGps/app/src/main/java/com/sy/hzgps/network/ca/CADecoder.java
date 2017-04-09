package com.sy.hzgps.network.ca;




import com.sy.hzgps.protocol.SY808Message;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class CADecoder extends CumulativeProtocolDecoder {

	private Logger logger = LoggerFactory.getLogger(CADecoder.class);
	private final Charset charset;
	private CACommHelper commHelper;


	public CADecoder(CACommHelper commHelper) {
		this(Charset.defaultCharset(), commHelper);
	}

	public CADecoder(Charset charset, CACommHelper commHelper) {
		this.charset = charset;
		this.commHelper = commHelper;
	}

	@Override
	protected boolean doDecode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput out) throws Exception {

		if (ioBuffer.remaining() < 1) {
			return false;
		}
		ioBuffer.mark();
		byte[] data = new byte[ioBuffer.remaining()];
		ioBuffer.get(data);
		// this.logger.warn(Tools.ToHexString(data));
		int pos = 0;
		ioBuffer.reset();
		while (ioBuffer.remaining() > 0) {
			ioBuffer.mark();
			byte tag = ioBuffer.get();
			// 搜索包的开始位置
			if (tag == 0x7E && ioBuffer.remaining() > 0) {
				tag = ioBuffer.get();
				// 防止是两个0x7E,取后面的为包的开始位置
				// 寻找包的结束
				while (tag != SY808Message.Prefix) {
					if (ioBuffer.remaining() <= 0) {
						ioBuffer.reset(); // 没有找到结束包，等待下一次包
						//logger.error("Half Packet:" + Tools.ToHexString(data));
						return false;
					}
					tag = ioBuffer.get();
				}
				pos = ioBuffer.position();
				int packetLength = pos - ioBuffer.markValue();
				if (packetLength > 1) {
					byte[] tmp = new byte[packetLength];



					ioBuffer.reset();
					ioBuffer.get(tmp);

					SY808Message message = new SY808Message();
					message.readFromBytes(tmp);
					out.write(message); // 触发接收Message的事件
				} else {
					// 说明是两个0x7E
					ioBuffer.reset();
					ioBuffer.get(); // 两个7E说明前面是包尾，后面是包头
				}
			}
		}

		return false;

	}

	/*
	 * @Override public void decode(IoSession ioSession, IoBuffer ioBuffer,
	 * ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
	 * 
	 * 
	 * Thread current = Thread.currentThread();
	 * 
	 * logger.info("decoder thread id = " + current.getName());
	 * 
	 * byte[] b = new byte[ioBuffer.limit()]; ioBuffer.get(b);
	 * 
	 * String v = new String(b); protocolDecoderOutput.write(v);
	 * 
	 * }
	 * 
	 * @Override public void finishDecode(IoSession ioSession,
	 * ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
	 * 
	 * }
	 * 
	 * @Override public void dispose(IoSession ioSession) throws Exception {
	 * 
	 * }
	 */
}
