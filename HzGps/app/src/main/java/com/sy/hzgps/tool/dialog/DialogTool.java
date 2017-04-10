package com.sy.hzgps.tool.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sy.hzgps.R;

/**
 * Created by Trust on 2017/4/9.
 */

public class DialogTool {
    static Dialog dialog;
    static ImageView QRcode;
    static TextView timeTv;
    static View view;
    public DialogTool() {

    }

    public static void showDialog(Context context, int layout, Bitmap bitmap,String time){

        if(dialog  == null){
            view = LayoutInflater.from(context).inflate(layout,null);
            QRcode = (ImageView) view.findViewById(R.id.dialog_qr);
            timeTv = (TextView) view.findViewById(R.id.dialog_time);
            Button button = (Button) view.findViewById(R.id.dialog_btn);


        /*
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setView(view);
        final AlertDialog dialog ;
        dialog = alertDialog.show();
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        */
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
}
