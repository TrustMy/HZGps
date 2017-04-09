package com.sy.hzgps;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.RemoteController;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.sy.hzgps.tool.L;
import com.zxing.activity.CaptureActivity;
import com.zxing.encoding.EncodingHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class QRCodeActivity extends BaseActivity {
    private ImageView imageView ,SDimg;
    private static final int SELECT_PIC = 0;
    private static final int SCAN_PIC = 1;

    private String path="/sdcard/erWeiMa.JPEG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        imageView = (ImageView) findViewById(R.id.erweima);
        SDimg = (ImageView) findViewById(R.id.sd_erweima);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void test(View v)
    {
        Bitmap qrBitmap = null;
        try {
            qrBitmap = EncodingHandler.createQRCode("{name:张三,starttime:2017327,endtime:2047327,num:12345678912456789}", 400);
            imageView.setImageBitmap(qrBitmap);
            saveMyBitmap("erWeiMa",qrBitmap);

                ExifInterface exifInterface = new ExifInterface(path);
                exifInterface.setAttribute(ExifInterface.TAG_DATETIME,"2017/3/29");
                exifInterface.saveAttributes();

        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void getBitmap(View view)
    {
        Bitmap bitmap = getSDBitmap();
        if(bitmap != null)
        {
            SDimg.setImageBitmap(bitmap);


            ExifInterface exifInterface = null;
            try {
                exifInterface = new ExifInterface(path);

                String TAG_APERTURE = exifInterface.getAttribute(ExifInterface.TAG_APERTURE);
                String TAG_DATETIME = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                String TAG_EXPOSURE_TIME = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
                String TAG_FLASH = exifInterface.getAttribute(ExifInterface.TAG_FLASH);
                String TAG_FOCAL_LENGTH = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
                String TAG_IMAGE_LENGTH = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                String TAG_IMAGE_WIDTH = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
                String TAG_ISO = exifInterface.getAttribute(ExifInterface.TAG_ISO);
                String TAG_MAKE = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
                String TAG_MODEL = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
                String TAG_ORIENTATION = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
                String TAG_WHITE_BALANCE = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
                L.d("光圈值:" + TAG_APERTURE);
                L.d("拍摄时间:" + TAG_DATETIME);
                L.d("曝光时间:" + TAG_EXPOSURE_TIME);
                L.d("闪光灯:" + TAG_FLASH);
                L.d("焦距:" + TAG_FOCAL_LENGTH);
                L.d("图片高度:" + TAG_IMAGE_LENGTH);
                L.d("图片宽度:" + TAG_IMAGE_WIDTH);
                L.d("ISO:" + TAG_ISO);
                L.d("设备品牌:" + TAG_MAKE);
                L.d("设备型号:" + TAG_MODEL);
                L.d("旋转角度:" + TAG_ORIENTATION);
                L.d("白平衡:" + TAG_WHITE_BALANCE);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }else
        {
            Toast.makeText(this, "未找到订单二维码!", Toast.LENGTH_SHORT).show();
        }
    }


    public  Bitmap getSDBitmap()
    {
        File mFile=new File(path);
        //若该文件存在
        if (mFile.exists()) {
            Bitmap bitmap=BitmapFactory.decodeFile(path);
            return bitmap;
        }else
        {
            return null;
        }
    }





    //将图像保存到SD卡中
    public void saveMyBitmap(String bitName,Bitmap mBitmap){
        File f = new File("/sdcard/" + bitName + ".JPEG");
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
    private Bitmap generateBitmap(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
    */


    public void check(View v)
    {
        Intent intent1 = new Intent();
        intent1.setAction(Intent.ACTION_PICK);
        intent1.setType("image/*");
        startActivityForResult(Intent.createChooser(intent1, "选择二维码图片"), SELECT_PIC);
    }

    public void check2(View v)
    {
        Intent intent3 = new Intent(QRCodeActivity.this, CaptureActivity.class);
        startActivityForResult(intent3, SCAN_PIC);
    }




    //解析二维码图片,返回结果封装在Result对象中
    private Result parseQRcodeBitmap(Bitmap bitmap){
        //解析转换类型UTF-8
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        //新建一个RGBLuminanceSource对象，将bitmap图片传给此对象
        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(bitmap);
        //将图片转换成二进制图片
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
        //初始化解析对象
        QRCodeReader reader = new QRCodeReader();
        //开始解析
        Result result = null;
        try {
            result = reader.decode(binaryBitmap, hints);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return result;
    }




    //解析二维码图片,返回结果封装在Result对象中
    private Result parseQRcodeBitmap(String bitmapPath){
        //解析转换类型UTF-8
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath,options);
        options.inSampleSize = options.outHeight / 400;
        if(options.inSampleSize <= 0){
            options.inSampleSize = 1; //防止其值小于或等于0
        }
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(bitmapPath, options);
        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(bitmap);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(binaryBitmap, hints);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case SELECT_PIC:
                    String[] proj = new String[]{MediaStore.Images.Media.DATA};
                    Cursor cursor = QRCodeActivity.this.getContentResolver().query(data.getData(), proj, null, null, null);
                    String imgPath = null;
                    if(cursor.moveToFirst()){
                        int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                        System.out.println(columnIndex);
                        imgPath = cursor.getString(columnIndex);
                    }
                    cursor.close();
                    Result result = parseQRcodeBitmap(imgPath);
                    Toast.makeText(QRCodeActivity.this,"解析结果：SELECT_PIC" + result, Toast.LENGTH_LONG).show();
                    Log.d("lhh", "onActivityResult  SELECT_PIC: "+result.getText());
                    break;
                case SCAN_PIC:
                    String resultq = data.getExtras().getString("result");
                    Toast.makeText(QRCodeActivity.this,"解析结果：SCAN_PIC" + resultq, Toast.LENGTH_LONG).show();
                    Log.d("lhh", "onActivityResult  SCAN_PIC: "+resultq.toString());
                    break;

                default:
                    break;
            }
        }

    }

}
