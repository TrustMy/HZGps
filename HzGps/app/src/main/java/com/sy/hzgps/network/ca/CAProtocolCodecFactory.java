package com.sy.hzgps.network.ca;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import java.nio.charset.Charset;

/**
 * Created by jiayang on 2016/1/20.
 */
public class CAProtocolCodecFactory implements ProtocolCodecFactory {

    private final CAEncoder encoder;

    private final CADecoder decoder;


    public CAProtocolCodecFactory(CACommHelper commHelper) {
        this(Charset.defaultCharset(), commHelper);
    }


    public CAProtocolCodecFactory(Charset charset, CACommHelper commHelper) {

        encoder = new CAEncoder(charset, commHelper);
        decoder = new CADecoder(charset, commHelper);

    }



    public ProtocolEncoder getEncoder(IoSession session) {
        return encoder;
    }

    public ProtocolDecoder getDecoder(IoSession session) {
        return decoder;
    }

}
