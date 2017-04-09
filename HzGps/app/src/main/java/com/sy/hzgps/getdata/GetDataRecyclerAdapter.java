package com.sy.hzgps.getdata;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sy.hzgps.MainActivity;
import com.sy.hzgps.R;

import java.util.List;

/**
 * Created by Trust on 2017/4/8.
 */

public class GetDataRecyclerAdapter extends RecyclerView.Adapter<GetDataRecyclerAdapter.ViewHolder> {
    private List<String> ml ;

    public void setMl(List<String> ml) {
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
                t.click(v,ml.get(viewHolder.getAdapterPosition()));
                /*
                int pos = viewHolder.getAdapterPosition();
                String msgItem = ml.get(pos);
                Toast.makeText(v.getContext(),"you clik item"+msgItem,Toast.LENGTH_LONG).show();
                v.getContext().startActivity(new Intent(v.getContext(), MainActivity.class));
                */
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.msgTv.setText(ml.get(position));
    }

    @Override
    public int getItemCount() {
        return ml!=null?ml.size():0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView msgTv;
        View views;
        public ViewHolder(View itemView) {
            super(itemView);
            views = itemView;
            msgTv = (TextView) itemView.findViewById(R.id.get_recycler_item_msg);
        }
    }
    


    interface test{
        void  click(View v,String msg);
    }
    test t;


}
