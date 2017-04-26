package com.sy.hzgps.getdata;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sy.hzgps.Config;
import com.sy.hzgps.MainActivity;
import com.sy.hzgps.MyContext;
import com.sy.hzgps.R;
import com.sy.hzgps.bean.OrderBean;
import com.sy.hzgps.tool.lh.L;
import com.sy.hzgps.tool.lh.T;

import java.util.List;

/**
 * Created by Trust on 2017/4/8.
 */

public class GetDataRecyclerAdapter extends RecyclerView.Adapter<GetDataRecyclerAdapter.ViewHolder> {
    private List<OrderBean> ml ;

    public void setMl(List<OrderBean> ml) {
        this.ml = ml;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.
        get_date_item,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.views.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                t.click(v,ml.get(viewHolder.getpo));
                /*
                int pos = viewHolder.getAdapterPosition();
                String msgItem = ml.get(pos);
                Toast.makeText(v.getContext(),"you clik item"+msgItem,Toast.LENGTH_LONG).show();
                v.getContext().startActivity(new Intent(v.getContext(), MainActivity.class));
                */
            }
        });
        viewHolder.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = viewHolder.getAdapterPosition();
//                T.showToast(view.getContext(),"你点击了订单号:"+ml.get(pos).getOrder());
                t.click(view,ml.get(pos));
            }
        });
        viewHolder.foundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = viewHolder.getAdapterPosition();
                t.click(view,ml.get(pos));
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        L.d(ml.get(position).getStatus()+"");
        holder.termIdTv.setText(ml.get(position).getTermId()+"");
        holder.orderTv.setText(ml.get(position).getOrder());
        holder.timeTv.setText(ml.get(position).getTime());
        holder.startTv.setText(ml.get(position).getStartName());
        holder.endTv.setText(ml.get(position).getEndName());
        int status = ml.get(position).getStatus();
        if(status == Config.SAVE_STATUS_SUCCESS){
            holder.statusTv.setTextColor(Color.parseColor("#98E165"));
            holder.statusTv.setText("提交成功");
            holder.submitBtn.setVisibility(View.GONE);
            holder.foundBtn.setVisibility(View.VISIBLE);
        }else {
            holder.statusTv.setTextColor(Color.parseColor("#ff0000"));
            holder.statusTv.setText("提交失败");
            holder.submitBtn.setVisibility(View.VISIBLE);
            holder.foundBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return ml!=null?ml.size():0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView termIdTv,orderTv,startTv,endTv,timeTv,statusTv;
        Button submitBtn,foundBtn;
        View views;
        public ViewHolder(View itemView) {
            super(itemView);
            views = itemView;
            foundBtn = (Button) itemView.findViewById(R.id.get_recycler_item_found_qr);
            termIdTv = (TextView) itemView.findViewById(R.id.get_recycler_item_termid);
            orderTv = (TextView) itemView.findViewById(R.id.get_recycler_item_order);
            startTv = (TextView) itemView.findViewById(R.id.get_recycler_item_start);
            endTv = (TextView) itemView.findViewById(R.id.get_recycler_item_end);
            timeTv = (TextView) itemView.findViewById(R.id.get_recycler_item_time);
            statusTv = (TextView) itemView.findViewById(R.id.get_recycler_item_status);
            submitBtn = (Button) itemView.findViewById(R.id.get_recycler_item_submit);

        }
    }
    


    interface test{
        void  click(View v,OrderBean orderBean);
    }
    test t;


}
