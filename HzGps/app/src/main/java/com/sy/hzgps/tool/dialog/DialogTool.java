package com.sy.hzgps.tool.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sy.hzgps.ApkConfig;
import com.sy.hzgps.R;

/**
 * Created by Trust on 2017/4/9.
 */

public class DialogTool {
    static Dialog dialog;
    static ImageView QRcode;
    static TextView timeTv;
    static View view;

    public static Dialog photoDialog;
    static ImageView Photo;
    static View PhotoView;
    static TextView phototime;
    public DialogTool() {

    }
    public static  click onClick;
      public interface click {
        void onClick();
    }


    /**
     * 订单dialog
     * @param context
     * @param layout
     * @param bitmap
     * @param time
     */
    public static void showDialog(Context context, int layout, Bitmap bitmap,String time){

        if(dialog  == null){
            view = LayoutInflater.from(context).inflate(layout,null);
            QRcode = (ImageView) view.findViewById(R.id.dialog_qr);
            timeTv = (TextView) view.findViewById(R.id.dialog_time);
            Button button = (Button) view.findViewById(R.id.dialog_btn);

            dialog = new Dialog(context,R.style.customDialog);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();


                }
            });
        }
        QRcode.setImageBitmap(bitmap);
        timeTv.setText("生成时间:\n"+time);
        dialog.setContentView(view);
//        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.show();

    }


    public static void showPhotoDialog(Context context, int layout, Bitmap bitmap,String time){

            PhotoView = LayoutInflater.from(context).inflate(layout,null);
            Photo = (ImageView) PhotoView.findViewById(R.id.dialog_photo_img);
            phototime = (TextView) PhotoView.findViewById(R.id.dialog_photo_time);
            Button determine = (Button) PhotoView.findViewById(R.id.dialog_photo_determine);
            Button cancel = (Button) PhotoView.findViewById(R.id.dialog_photo_cancel);
            photoDialog = new Dialog(context,R.style.customDialog);
            determine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phoneOnClick.onClick(v);
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photoDialog.dismiss();
                }
            });



        Photo.setImageBitmap(bitmap);
        phototime.setText("生成时间:\n"+time);
        photoDialog.setContentView(PhotoView);
//        dialog.setCancelable(true);
        photoDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        photoDialog.show();
    }

    public interface PhoneOnClick {
        void onClick(View v);
    }

    public static PhoneOnClick phoneOnClick;
}
