package com.ccg.emaildemo.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.ccg.emaildemo.R;

public class StringAdapter extends BaseAdapter<String> {

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public StringAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_string;
    }

    @Override
    protected void bindView(BaseViewHolder holder, String data, final int position) {
        TextView textView = holder.getView(R.id.tv_string);
        if (data != null){
            textView.setText(data);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(v,position);
                }
            }
        });
    }
}
