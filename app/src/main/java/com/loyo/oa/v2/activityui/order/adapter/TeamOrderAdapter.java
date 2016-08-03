package com.loyo.oa.v2.activityui.order.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.OrderCommon;
import com.loyo.oa.v2.activityui.order.bean.OrderListItem;
import com.loyo.oa.v2.tool.DateTool;

import java.util.List;

/**
 * 团队订单列表的adapter
 * Created by xeq on 16/8/2.
 */
public class TeamOrderAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    List<OrderListItem> data;

    public TeamOrderAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<OrderListItem> records) {
        this.data = records;
        notifyDataSetChanged();
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
            convertView = inflater.inflate(R.layout.item_order_my_team, null);
            holder = new Holder();
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            holder.tv_customer = (TextView) convertView.findViewById(R.id.tv_customer);
            holder.tv_product = (TextView) convertView.findViewById(R.id.tv_product);
            holder.ll_responsible = (LinearLayout) convertView.findViewById(R.id.ll_responsible);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.ll_responsible.setVisibility(View.VISIBLE);
        OrderListItem mData = data.get(position);
        holder.tv_title.setText(mData.title);
        OrderCommon.getOrderStatus(holder.tv_status, mData.status);
        holder.tv_name.setText(mData.directorName);
        holder.tv_money.setText(mData.dealMoney + "");
        holder.tv_customer.setText(mData.customerName);
        holder.tv_product.setText(mData.proName);
        holder.tv_time.setText(DateTool.getDiffTime(Long.valueOf(mData.createdAt + "") * 1000));
        return convertView;
    }

    class Holder {
        TextView tv_title, tv_status, tv_time, tv_name, tv_money, tv_customer, tv_product;
        LinearLayout ll_responsible;
    }
}
