package com.sy.hzgps.tool.lh;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**图片转换,压缩
 * Created by Trust on 2017/4/13.
 */
public class BitmapAndStringUtils {
    /**
     * 图片转成string
     *
     * @param bitmap
     * @return
     */
    public static String convertIconToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }

    /**
     * string转成bitmap
     *
     * @param st
     */
    public static Bitmap convertStringToIcon(String st)
    {
        // OutputStream out;
        Bitmap bitmap = null;
        try
        {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        }
        catch (Exception e)
        {
            return null;
        }
    }


    /**
     * bitmap 压缩,旋转,保存
     * @param fileName
     * @param filePath
     * @return
     */
    public static Bitmap bitmapCompression(String fileName,String filePath){
        try{
            int quality = 80;
            Bitmap bitmap = null;
            //
            File f = new File(filePath,fileName + ".jpg");
            int rotate = 0;
            try {
                ExifInterface exifInterface = new ExifInterface(filePath);
                int result = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                switch(result) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                    default:
                        rotate =  0;
                        break;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            L.d("rotate :"+rotate);
            // 1:compress bitmap
            try {
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(new FileInputStream(f), null, o);
                int width_tmp = o.outWidth, height_tmp = o.outHeight;
                int scale = 1;
                while (true) {
                    int VARIETY_SIZE = (rotate==90 || rotate==270)?height_tmp:width_tmp;
                    if (VARIETY_SIZE / 2 <= 600){
                        if(VARIETY_SIZE>600 && VARIETY_SIZE-600>300){
                        }else{
                            break;
                        }
                    }
                    width_tmp /= 2;
                    height_tmp /= 2;
                    scale *= 2;
                }
                // decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                bitmap =  BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
                int max = width_tmp>height_tmp?width_tmp:height_tmp;
                int min = width_tmp>height_tmp?height_tmp:width_tmp;
                int value = (int)((float)max*10/min);
                if(value>15){
                    quality = 80;
                }else{
                    quality = 90;
                }
            } catch (FileNotFoundException e) {
                System.gc();
            } catch(OutOfMemoryError e){
                System.gc();
                e.printStackTrace();
            }
            // 2:rotate bitmap
            if(f.exists()){
                f.delete();
            }
            if(rotate>0){
                Matrix mtx = new Matrix();
                mtx.postRotate(rotate);
                try{
                    Bitmap roateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(), mtx, true);
                    if(roateBitmap!=null && roateBitmap!=bitmap){
                        bitmap.recycle();
                        bitmap = null;
                        System.gc();
                        bitmap = roateBitmap;
                    }
                }catch(OutOfMemoryError e){
                    System.gc();
                    e.printStackTrace();
                }
            }
            L.d("onActivityResult: bitmap:"+bitmap.toString()
                    +"|bitmap2 大小:"+(bitmap.getByteCount() / 1024 )+"KB");

            String img = BitmapAndStringUtils.convertIconToString(bitmap);

                    L.d("String img :"+(img.getBytes().length / 1024) + "kb");



            /*
            压缩后的bitmap  保存到指定的路径
            // 3:save bitmap
            FileOutputStream fileOutputStream = new FileOutputStream(f.getPath());
            BufferedOutputStream os = new BufferedOutputStream(fileOutputStream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, os);
            bitmap.recycle();
            os.flush();
            os.close();
            */

            System.gc();
            return bitmap;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
