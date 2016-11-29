package com.loyo.oa.v2.activityui.customer.adapter;


import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.permission.Permission;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewHolder;

import java.util.ArrayList;
import java.util.Date;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【公海客户】适配器
 * Created by yyy on 16/6/2.
 */
public class CommCustomerAdapter extends BaseAdapter {

    private ArrayList<Customer> mCustomers;
    private Context mContext;
    private Permission permission;
    private Handler mHandler;

    public CommCustomerAdapter(final Context context, ArrayList<Customer> customers, Handler mHandler) {
        this.mHandler = mHandler;
        mCustomers = customers;
        mContext = context;
    }

    public void permissionTest(ImageView img) {
        permission = MainApp.rootMap.get("0404");
            /*超级管理员/Web控制权限判断*/
        if ((null != permission && permission.isEnable()) || MainApp.user.isSuperUser()) {
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
        img_public.setVisibility(View.INVISIBLE);
        permissionTest(img_public);
        layout_go_where.setVisibility(View.GONE);
        layout2.setVisibility(View.VISIBLE);
        layout3.setVisibility(View.GONE);
        img1.setImageResource(R.drawable.icon_customer_tag);
        img2.setImageResource(R.drawable.icon_customer_follow_time);
        tv_content1.setText("标签：" + tagItems);
        tv_content2.setText("跟进时间：" + lastActivityAt);


        img_public.setOnTouchListener(Global.GetTouch());
        img_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//挑入公海客户
                RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                        create(ICustomer.class).pickedIn(customer.getId(), new RCallback<Customer>() {
                    @Override
                    public void success(Customer customer, Response response) {
                        HttpErrorCheck.checkResponse(response);
                        mHandler.sendEmptyMessage(CustomerManagerActivity.CUSTOMER_COMM_RUSH);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
            }
        });

        return convertView;
    }
}
