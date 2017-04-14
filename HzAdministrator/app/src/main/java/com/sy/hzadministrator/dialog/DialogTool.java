package com.sy.hzadministrator.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sy.hzadministrator.R;
import com.sy.hzadministrator.db.QrBean;


/**
 * Created by Trust on 2017/4/9.
 */

public class DialogTool {
    static Dialog dialog;
    static View view;
    static TextView termId = null,order = null,status,start = null,end = null,timeTv = null;
    public DialogTool() {

    }
    public static  click onClick;
      public interface click {
        void onClick();
    }

    public static void showDialog(Context context, int layout,QrBean qrBean){

        if(dialog  == null){
            view = LayoutInflater.from(context).inflate(layout,null);

            termId = (TextView) view.findViewById(R.id.dialog_termid);
            order = (TextView) view.findViewById(R.id.dialog_order);
            start = (TextView) view.findViewById(R.id.dialog_start);
            end = (TextView) view.findViewById(R.id.dialog_end);
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
                    onClick.onClick();
                }
            });
        }

        termId.setText(qrBean.getTermId()+"");
        timeTv.setText(qrBean.getTime());
        start.setText(qrBean.getStartName());
        end.setText(qrBean.getEndName());
        order.setText(qrBean.getOrder());



        dialog.setContentView(view);
//        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.show();

    }
}
