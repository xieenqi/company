package com.loyo.oa.v2.activityui.product.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.loyo.oa.common.utils.DensityUtil;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.PreviewImageListActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.sql.Savepoint;
import java.util.List;

/**
 * ［新增购买产品］ 产品图片 适配器
 * Created by jie on 16/12/22.
 */

public class ProductPicAdapter extends BaseAdapter {
    public ProductPicAdapter(Context context,List<Attachment> data) {
        this.data = data;
        this.context = context;
    }

    private List<Attachment> data;
    private Context context;

    public List<Attachment> getData() {
        return data;
    }
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(null==convertView){
            //新建一个图片资源
            convertView=LayoutInflater.from(context).inflate(R.layout.item_product_pic,null);
            imageView= (ImageView) convertView.findViewById(R.id.product_iv_image);
            convertView.setTag(imageView);
//            imageView=new ImageView(context);
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,DensityUtil.dp2px(context,75));
//            imageView.setLayoutParams(layoutParams);
//            imageView.setImageResource(R.drawable.default_image);
//            imageView.setAdjustViewBounds(true);
        }else{
            imageView= (ImageView) convertView.getTag();
        }
        ImageLoader.getInstance().displayImage(data.get(position).getUrl(),imageView);

        return convertView;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    public void setData(List<Attachment> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }
}
