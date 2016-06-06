package com.loyo.oa.v2.activity.customer.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewHolder;

import java.util.ArrayList;
import java.util.Date;

/**
 * 【团队客户】适配器
 * Created by yyy on 16/6/2.
 */
public class AdapterTeamCustomer extends BaseAdapter {

    private ArrayList<Customer> mCustomers;
    private Context mContext;

    public AdapterTeamCustomer(final Context context, ArrayList<Customer> customers) {
        mCustomers = customers;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mCustomers.size();
    }

    @Override
    public Object getItem(int position) {
        return mCustomers.isEmpty() ? null : mCustomers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Customer customer = mCustomers.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_common_customer, null, false);
        }
        TextView tv_title = ViewHolder.get(convertView, R.id.tv_title);
        TextView tv_content1 = ViewHolder.get(convertView, R.id.tv_content1);
        TextView tv_content2 = ViewHolder.get(convertView, R.id.tv_content2);
        TextView tv_content3 = ViewHolder.get(convertView, R.id.tv_content3);
        TextView tv_content4 = ViewHolder.get(convertView, R.id.tv_distance);

        ImageView img1 = ViewHolder.get(convertView, R.id.img_1);
        ImageView img2 = ViewHolder.get(convertView, R.id.img_2);
        ImageView img3 = ViewHolder.get(convertView, R.id.img_3);
        ImageView imgWin = ViewHolder.get(convertView, R.id.iv_win);
        ImageView img_public = ViewHolder.get(convertView, R.id.img_public);
        ImageView img_go_where = ViewHolder.get(convertView, R.id.img_go_where);


        ViewGroup layout1 = ViewHolder.get(convertView, R.id.layout_1);
        ViewGroup layout2 = ViewHolder.get(convertView, R.id.layout_2);
        ViewGroup layout3 = ViewHolder.get(convertView, R.id.layout_3);
        ViewGroup layout_go_where = ViewHolder.get(convertView, R.id.layout_go_where);

        tv_title.setText(customer.name);
        String tagItems = Utils.getTagItems(customer);
        String lastActivityAt = MainApp.getMainApp().df3.format(new Date(customer.lastActAt * 1000));

        img_public.setVisibility(View.GONE);
        layout_go_where.setVisibility(View.GONE);
        layout2.setVisibility(View.VISIBLE);
        layout3.setVisibility(View.VISIBLE);

        img1.setImageResource(R.drawable.icon_customer_tag);
        img2.setImageResource(R.drawable.icon_follow_up_creator);
        img3.setImageResource(R.drawable.icon_customer_follow_time);

        String responser = null == customer.owner || null == customer.owner ? "" : customer.owner.name;
        tv_content1.setText("标签：" + tagItems);
        tv_content2.setText("负责人：" + responser);
        tv_content3.setText("跟进时间：" + lastActivityAt);


        return convertView;
    }
}
