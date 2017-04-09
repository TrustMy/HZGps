package com.sy.hzgps.tool.qrcode;

import android.graphics.Bitmap;

import com.google.zxing.WriterException;
import com.zxing.encoding.EncodingHandler;

/**
 * Created by Trust on 2017/4/8.
 */

public class QRcodeTool {

    public static Bitmap getQRcode(String msg ,int size) throws WriterException {
        Bitmap bitmap = EncodingHandler.createQRCode(msg, size);
        return bitmap;
    }

}
