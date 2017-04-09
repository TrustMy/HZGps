package com.sy.hzgps.protocol;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class MyBuffer {
	
	ByteBuffer buff;

	public MyBuffer() {
		buff = ByteBuffer.allocate(4096);
		buff.mark();
	}

	public MyBuffer(byte[] bytes) {
		if (bytes.length > 4096)
			buff = ByteBuffer.allocate(bytes.length + 100);
		else
			buff = ByteBuffer.allocate(4096);
		buff.mark();
		buff.put(bytes);
		buff.limit(bytes.length);
		buff.reset();
	}

	public void clear() {
		buff.clear();
		buff.mark();
	}

	public void putByte(byte a) {
		buff.put(a);
	}

	public void putByteArray(byte[] a) {
		buff.put(a);
	}

	
	public void putShort(short a) {
		buff.putShort(a);
	}
	
	public void putShort(int a) {
		buff.putShort((short) a);
	}

	public void putInt(int a) {
		buff.putInt(a);
	}

	public void putLong(long a) {
		buff.putLong(a);
	}
	
	public void putString(String str) {
		// US-ASCII
		try {
			byte[] b = str.getBytes("gbk");
			buff.put(b);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void putString(String str, int len) {
		byte[] result = new byte[len];
		try {
			byte[] b = str.getBytes("gbk");

			System.arraycopy(b, 0, result, 0, b.length);

			for (int m = b.length; m < len; m++) {
				result[m] = 0;
			}
			buff.put(result);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public boolean hasRemain() {
		return buff.remaining() > 0;
	}

	
	

	public byte get() {
		return buff.get();
	}

	public byte[] gets(int len) {
		byte[] data = new byte[len];
		buff.get(data);
		return data;
	}

	public int getInt() {
		return buff.getInt();
	}
	
	public long getLong() {
		return buff.getLong();
	}

	public short getShort() {
		// byte b1 = buff.get();
		// byte b2 = buff.get();
		// short x = (short)(b2 << 8 + b1);
		// return x;
		return buff.getShort();
	}

	public String getString() {
		Charset charset = null;
        CharsetDecoder decoder = null;
        CharBuffer charBuffer = null;
        try
        {
            charset = Charset.forName("GBK");
            decoder = charset.newDecoder();
            // charBuffer = decoder.decode(buffer);//用这个的话，只能输出来一次结果，第二次显示为空
            charBuffer = decoder.decode(buff.asReadOnlyBuffer());
            return charBuffer.toString();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "";
        }
	}
	
	public String getString(int length) {
		try {
            byte[] tmp = new byte[length];
            buff = buff.get(tmp, 0, length);
            
            return new String(tmp, "GBK");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
	}

	public byte[] array() {
		int pos = buff.position();
		byte[] data = new byte[pos];
		buff.reset();
		buff.get(data);
		
		//buff.clear();
		return data;
	}

	public static void main(String[] args) {

	
	}

}
