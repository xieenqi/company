package com.loyo.oa.v2.activityui.followup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.loyo.oa.v2.R;

/**
 * 【跟进拜访】详情列表评论 公用Adapter
 * Created by yyy on 16/11/11.
 */

public class ListOrDetailsCommentAdapter extends BaseAdapter{

    private Context mContext;

    public  ListOrDetailsCommentAdapter(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(null == convertView){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_comment,null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.iv_calls = (TextView) convertView.findViewById(R.id.iv_calls);
            holder.tv_audio_length = (TextView) convertView.findViewById(R.id.tv_audio_length);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_name.setText("刘德华:");
        holder.iv_calls.setText("00000000");

        return convertView;
    }


    class ViewHolder{
        TextView tv_name;
        TextView iv_calls;
        TextView tv_audio_length;
    }


}
