package com.loyo.oa.v2.activityui.customer.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.common.CommonMethod;
import com.loyo.oa.v2.activityui.followup.FollowSelectActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewHolder;

import java.util.ArrayList;
import java.util.Date;

/**
 * 【我的客户】适配器
 * Created by yyy on 16/6/2.
 */
public class MyCustomerAdapter extends BaseAdapter {

    private ArrayList<Customer> mCustomers;
    private Context mContext;
    private LayoutInflater inflater;

    public MyCustomerAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    public MyCustomerAdapter(final Context context, ArrayList<Customer> customers) {
        mCustomers = customers;
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    public void setData(ArrayList<Customer> customers) {
        this.mCustomers = customers;
        notifyDataSetChanged();
    }

    public Customer getItemData(int position) {
        return mCustomers.get(position);
    }

    @Override
    public int getCount() {
        return null == mCustomers ? 0 : mCustomers.size();
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
            convertView = inflater.inflate(R.layout.item_list_customer, null, false);
        }
        TextView tv_title = ViewHolder.get(convertView, R.id.tv_title);
        TextView tv_content1 = ViewHolder.get(convertView, R.id.tv_content1);
        TextView tv_content2 = ViewHolder.get(convertView, R.id.tv_content2);
        TextView tv_content41 = ViewHolder.get(convertView, R.id.tv_content41);
        ViewGroup layout_4 = ViewHolder.get(convertView, R.id.layout_4);
        TextView tv_status = ViewHolder.get(convertView, R.id.tv_status);//状态
        tv_title.setText(customer.name);
        String lastActivityAt = DateTool.getDateTimeFriendly(customer.lastActAt);
        tv_status.setText(customer.statusName+"");//设置状态
        tv_content1.setText("标签：" + customer.displayTagString());
        tv_content2.setText("跟进时间：" + lastActivityAt);
        if (!(mContext instanceof FollowSelectActivity)){
            CommonMethod.commonCustomerRecycleTime(customer, layout_4, tv_content41);
        }else{
            tv_status.setVisibility(View.GONE);
        }
        return convertView;
    }

}
