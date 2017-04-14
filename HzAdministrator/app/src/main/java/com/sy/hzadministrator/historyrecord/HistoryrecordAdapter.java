package com.sy.hzadministrator.historyrecord;

import android.graphics.Color;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sy.hzadministrator.Config;
import com.sy.hzadministrator.L;
import com.sy.hzadministrator.R;
import com.sy.hzadministrator.db.OrderBean;

import java.util.List;

/**
 * Created by Trust on 2017/4/12.
 */
public class HistoryrecordAdapter extends RecyclerView.Adapter<HistoryrecordAdapter.ViewHolder> {
    private List<OrderBean> ml;

    public void setMl(List<OrderBean> ml) {
        this.ml = ml;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.
                item_history_record,parent,false);
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
                click.click(view,ml.get(pos));
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
        holder.workTimeTv.setText(ml.get(position).getWorkTime());
        int status = ml.get(position).getStatus();
        if(status == Config.SAVE_STATUS_SUCCESS){
            holder.statusTv.setTextColor(Color.parseColor("#98E165"));
            holder.statusTv.setText("提交成功");
            holder.submitBtn.setVisibility(View.GONE);
        }else {
            holder.statusTv.setTextColor(Color.parseColor("#ff0000"));
            holder.statusTv.setText("提交失败");
            holder.submitBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return ml!=null?ml.size():0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView termIdTv,orderTv,startTv,endTv,timeTv,statusTv,workTimeTv;
        Button submitBtn;
        View views;
        public ViewHolder(View itemView) {
            super(itemView);
            views = itemView;
            termIdTv = (TextView) itemView.findViewById(R.id.history_record_item_termid);
            orderTv = (TextView) itemView.findViewById(R.id.history_record_item_order);
            startTv = (TextView) itemView.findViewById(R.id.history_record_item_start);
            endTv = (TextView) itemView.findViewById(R.id.history_record_item_end);
            timeTv = (TextView) itemView.findViewById(R.id.history_record_item_time);
            statusTv = (TextView) itemView.findViewById(R.id.history_record_item_status);
            submitBtn = (Button) itemView.findViewById(R.id.history_record_item_submit);
            workTimeTv = (TextView) itemView.findViewById(R.id.history_record_item_work_time);
        }
    }

    interface Click{
        void  click(View v,OrderBean orderBean);
    }

    public Click click;
}
