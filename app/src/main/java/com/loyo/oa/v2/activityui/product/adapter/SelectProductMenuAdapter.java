package com.loyo.oa.v2.activityui.product.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.loyo.oa.v2.R;

/**
 * 选择产品分类适配器
 * 不用了
 * Created by yyy on 16/12/22.
 */

public class SelectProductMenuAdapter extends BaseAdapter{

    Context mContext;

    public SelectProductMenuAdapter(Context mContext){
        this.mContext = mContext;
    }


    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if(null == convertView){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_selectproduct_menu,null);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        return convertView;
    }


    class ViewHolder{
        CheckBox checkbox;
        TextView textView;
    }

}
