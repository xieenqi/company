package com.loyo.oa.v2.activityui.order.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.common.OrderCommon;
import com.loyo.oa.v2.activityui.order.bean.OrderListItem;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.Utils;

import java.util.List;

/**
 * 我的订单列表的adapter
 * Created by xeq on 16/8/2.
 */
public class MyOrderAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    List<OrderListItem> data;

    public MyOrderAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<OrderListItem> records) {
        this.data = records;
        notifyDataSetChanged();
    }

    public OrderListItem getItemData(int index) {
        return data.get(index);
    }

    @Override
    public int getCount() {
        return null == data ? 0 : data.size();
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
            convertView = inflater.inflate(R.layout.item_order_list_my_and_team, null);
            holder = new Holder();
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_customer = (TextView) convertView.findViewById(R.id.tv_customer);
            holder.tv_product = (TextView) convertView.findViewById(R.id.tv_product);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        OrderListItem mData = data.get(position);
        holder.tv_title.setText(mData.title);
        holder.tv_status.setText(OrderCommon.getOrderDetailsStatus(mData.status));
        holder.tv_customer.setText(mData.customerName);
        holder.tv_product.setText(mData.proName);
        holder.tv_time.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(Long.valueOf(mData.createdAt + "")));
        return convertView;
    }

    class Holder {
        TextView tv_title, tv_status, tv_time, tv_name, tv_money, tv_customer, tv_product;
    }
}
