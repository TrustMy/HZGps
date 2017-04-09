package com.sy.hzgps.tool.dialog;

import android.app.AlertDialog;
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

    public static void showDialog(Context context, int layout, Bitmap bitmap,String time){

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        View view = LayoutInflater.from(context).inflate(layout,null);
        ImageView QRcode = (ImageView) view.findViewById(R.id.dialog_qr);
        TextView timeTv = (TextView) view.findViewById(R.id.dialog_time);
        Button button = (Button) view.findViewById(R.id.dialog_btn);

        QRcode.setImageBitmap(bitmap);
        timeTv.setText(time);

        alertDialog.setView(view);
        final AlertDialog dialog ;
        dialog = alertDialog.show();
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
