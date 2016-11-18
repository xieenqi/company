package com.loyo.oa.v2.activityui.followup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * 【跟进拜访】详情和列表 图片适配器
 * Created by yyy on 16/11/12.
 */

public class ListOrDetailsGridViewAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<Attachment> attachments;


    public ListOrDetailsGridViewAdapter(Context mContext,ArrayList<Attachment> attachments){
        this.mContext = mContext;
        this.attachments = attachments;
    }


    @Override
    public int getCount() {
        return attachments.size();
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
        Attachment mAttachment = attachments.get(position);
        if(null == convertView){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gridview_dl,null);
            holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        ImageLoader.getInstance().displayImage(mAttachment.getUrl(),holder.iv_image);

        return convertView;
    }

    class ViewHolder{
        ImageView iv_image;
    }

}
