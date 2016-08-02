package com.loyo.oa.v2.activityui.order.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;

/**
 * 我的订单列表的adapter
 * Created by xeq on 16/8/2.
 */
public class MyOrderAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;

    public MyOrderAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_order_my_team, null);
            holder = new Holder();
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            holder.tv_customer = (TextView) convertView.findViewById(R.id.tv_customer);
            holder.tv_product = (TextView) convertView.findViewById(R.id.tv_product);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        return convertView;
    }

    class Holder {
        TextView tv_title, tv_status, tv_time, tv_name, tv_money, tv_customer, tv_product;
    }
}
