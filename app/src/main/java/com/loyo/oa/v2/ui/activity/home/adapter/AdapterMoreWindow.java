package com.loyo.oa.v2.ui.activity.home.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.ui.activity.home.bean.MoreWindowItem;
import com.loyo.oa.v2.common.Global;
import java.util.ArrayList;

/**
 * 【首页菜单】 gridView适配器
 * Created by yyy on 16/6/17.
 */
public class AdapterMoreWindow extends BaseAdapter{

    public ArrayList<MoreWindowItem> data;
    public Context mContext;
    public Handler mHandler;

    public AdapterMoreWindow(ArrayList<MoreWindowItem> data,Context mContext,Handler mHandler){
        this.data = data;
        this.mHandler = mHandler;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(null == convertView){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_morewindow,null);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.case_morewindow_img);
            holder.name = (TextView) convertView.findViewById(R.id.case_morewindow_tv);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        MoreWindowItem item = data.get(position);

        holder.img.setImageResource(item.img);
        holder.name.setText(item.name);

        holder.img.setOnTouchListener(Global.GetTouch());
        return convertView;
    }

    class ViewHolder {
        public ImageView img;
        public TextView  name;
    }
}
