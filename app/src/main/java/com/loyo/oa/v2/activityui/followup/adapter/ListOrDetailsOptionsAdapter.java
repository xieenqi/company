package com.loyo.oa.v2.activityui.followup.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.customview.XCRoundRectImageView;
import com.loyo.oa.v2.tool.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * 【跟进拜访】详情和列表 文件适配器
 * Created by yyy on 16/11/12.
 */

public class ListOrDetailsOptionsAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<Attachment> attachments;

    public ListOrDetailsOptionsAdapter(Context mContext,ArrayList<Attachment> attachments){
        this.mContext = mContext;
        this.attachments = attachments;
    }


    @Override
    public int getCount() {
        return attachments.size();
    }

    @Override
    public Object getItem(int position) {
        return attachments.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_dynamic_listorlist,null);
            holder.iv_image = (XCRoundRectImageView) convertView.findViewById(R.id.iv_image);
            holder.tv_image_name = (TextView) convertView.findViewById(R.id.tv_image_name);
            holder.tv_image_size = (TextView) convertView.findViewById(R.id.tv_image_size);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        /*文件图片*/
        if(null != mAttachment.getUrl()){
            ImageLoader.getInstance().displayImage(mAttachment.getUrl(),holder.iv_image);
        }
        holder.tv_image_name.setText(mAttachment.getName());
        holder.tv_image_size.setText("大小:" + Utils.FormetFileSize(Long.valueOf(mAttachment.getSize())));

        return convertView;
    }

    class ViewHolder{
        XCRoundRectImageView iv_image;
        TextView tv_image_name;
        TextView tv_image_size;
    }

}
