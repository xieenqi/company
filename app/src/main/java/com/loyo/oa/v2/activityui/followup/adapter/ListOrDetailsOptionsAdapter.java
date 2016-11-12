package com.loyo.oa.v2.activityui.followup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.customview.XCRoundRectImageView;

/**
 * 【跟进拜访】详情和列表 附件适配器
 * Created by yyy on 16/11/12.
 */

public class ListOrDetailsOptionsAdapter extends BaseAdapter{

    private Context mContext;

    public ListOrDetailsOptionsAdapter(Context mContext){
        this.mContext = mContext;
    }


    @Override
    public int getCount() {
        return 4;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_dynamic_listorlist,null);
            holder.iv_image = (XCRoundRectImageView) convertView.findViewById(R.id.iv_image);
            holder.tv_image_name = (TextView) convertView.findViewById(R.id.tv_image_name);
            holder.tv_image_size = (TextView) convertView.findViewById(R.id.tv_image_size);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.iv_image.setBackgroundResource(R.drawable.app_launcher);
        holder.tv_image_name.setText("三体黑暗计划1");
        holder.tv_image_size.setText("25kb");

        return convertView;
    }

    class ViewHolder{
        XCRoundRectImageView iv_image;
        TextView tv_image_name;
        TextView tv_image_size;
    }

}
