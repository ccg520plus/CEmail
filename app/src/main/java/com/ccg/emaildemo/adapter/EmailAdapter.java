package com.ccg.emaildemo.adapter;

import android.content.Context;
import android.widget.TextView;

import com.ccg.emaillib.entries.Email;
import com.ccg.emaildemo.R;

public class EmailAdapter extends BaseAdapter<Email> {


    public EmailAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_email;
    }

    @Override
    protected void bindView(BaseViewHolder holder, Email data, int position) {
        TextView title = holder.getView(R.id.tv_subtitle);
        TextView from = holder.getView(R.id.tv_from);
        TextView datetime = holder.getView(R.id.tv_datetime);

        if (data.getTitle() != null){
            title.setText(data.getTitle());
        }

        if (data.getFrom() != null){
            from.setText(data.getFrom());
        }

        if (data.getDateTime() != null){
            datetime.setText(data.getDateTime());
        }

    }
}
