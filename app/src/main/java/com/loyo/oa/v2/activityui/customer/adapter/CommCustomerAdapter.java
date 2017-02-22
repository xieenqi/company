package com.loyo.oa.v2.activityui.customer.adapter;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.common.utils.UmengAnalytics;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.beans.BaseBean;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewHolder;

import java.util.ArrayList;

/**
 * 【公海客户】适配器
 * Created by yyy on 16/6/2.
 */
public class CommCustomerAdapter extends BaseAdapter {

    private ArrayList<Customer> mCustomers;
    private Context mContext;
    private LayoutInflater inflater;

    public CommCustomerAdapter(final Context context, ArrayList<Customer> customers) {
        mCustomers = customers;
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    private void permissionTest(ImageView img) {
        /* 客户挑入权限 */
        if (PermissionManager.getInstance().hasPermission(BusinessOperation.CUSTOMER_PICKING)) {
            img.setVisibility(View.VISIBLE);
        }
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Customer customer = mCustomers.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_common_customer, null, false);
        }

        TextView tv_title = ViewHolder.get(convertView, R.id.tv_title);
        TextView tv_content1 = ViewHolder.get(convertView, R.id.tv_content1);
        TextView tv_content2 = ViewHolder.get(convertView, R.id.tv_content2);
//        TextView tv_content3 = ViewHolder.get(convertView, R.id.tv_content3);
//        TextView tv_content4 = ViewHolder.get(convertView, R.id.tv_distance);

        ImageView img1 = ViewHolder.get(convertView, R.id.img_1);
        ImageView img2 = ViewHolder.get(convertView, R.id.img_2);
        ImageView img3 = ViewHolder.get(convertView, R.id.img_3);
        ImageView img_public = ViewHolder.get(convertView, R.id.img_public);
        ImageView img_go_where = ViewHolder.get(convertView, R.id.img_go_where);


//        ViewGroup layout1 = ViewHolder.get(convertView, R.id.layout_1);
        ViewGroup layout2 = ViewHolder.get(convertView, R.id.layout_2);
        ViewGroup layout3 = ViewHolder.get(convertView, R.id.layout_3);
        ViewGroup layout_go_where = ViewHolder.get(convertView, R.id.layout_go_where);

        tv_title.setText(customer.name);
        String recycledAt = customer.recycledAt != 0 ? DateTool.getDateTimeFriendly(customer.recycledAt) : "--";
        img_public.setVisibility(View.INVISIBLE);
        permissionTest(img_public);
        layout_go_where.setVisibility(View.GONE);
        layout2.setVisibility(View.VISIBLE);
        layout3.setVisibility(View.GONE);
        img1.setImageResource(R.drawable.icon_customer_tag);
        img2.setImageResource(R.drawable.icon_customer_follow_time);
        tv_content1.setText("标签：" + customer.displayTagString());
        tv_content2.setText("丢公海时间：" + recycledAt);


        img_public.setOnTouchListener(Global.GetTouch());
        img_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//挑入公海客户
                CustomerService.pickInCustomer(customer.getId())
                        .subscribe(new DefaultLoyoSubscriber<Customer>() {
                            @Override
                            public void onNext(Customer customer) {
                                mCustomers.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                UmengAnalytics.umengSend(mContext, UmengAnalytics.frompublicPublic);
            }
        });

        return convertView;
    }
}
