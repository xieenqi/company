package com.loyo.oa.v2.ui.activity.customer.adapter;


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
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 【附近客户】适配器
 * Created by yyy on 16/6/2.
 */
public class NearCustomerAdapter extends BaseAdapter{

    private ArrayList<Customer> mCustomers;
    private Context mContext;
    private int customer_type;
    private DecimalFormat df = new DecimalFormat("0.0");
    private boolean isNear = false;

    public NearCustomerAdapter(final Context context, ArrayList<Customer> customers, int customer_type){
        mCustomers = customers;
        mContext  = context;
        this.customer_type = customer_type;
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

        //附近 - 个人(我的客户)
        if (customer_type == Customer.CUSTOMER_TYPE_NEAR_MINE) {
            layout_go_where.setVisibility(View.VISIBLE);
            img_public.setVisibility(View.GONE);
            layout2.setVisibility(View.GONE);
            layout3.setVisibility(View.GONE);

            img1.setImageResource(R.drawable.icon_customer_tag);
            tv_content1.setText("标签：" + tagItems);

            if (null != customer.distance) {
                String distance;
                if (customer.distance.contains("km")) {
                    tv_content4.setText(df.format(Double.parseDouble(customer.distance.replace("km", ""))) + "km");
                } else if (customer.distance.contains("m")) {
                    double disa = Float.parseFloat(customer.distance.replace("m", ""));
                    if (disa <= 100) {
                        distance = "<0.1km";
                    } else {
                        double disb = Math.round((disa / 1000) * 10) / 10;
                        distance = disb + "km";
                    }
                    tv_content4.setText(distance);
                }
            } else {
                tv_content4.setText("无距离数据");
            }

            if (isNear && customer.winCount != 0) {
                imgWin.setVisibility(View.VISIBLE);
            }
        }
        //附近 - 团队
        else if (customer_type == Customer.CUSTOMER_TYPE_NEAR_TEAM) {
            layout_go_where.setVisibility(View.VISIBLE);
            img_public.setVisibility(View.GONE);
            layout2.setVisibility(View.GONE);
            layout3.setVisibility(View.GONE);

            img1.setImageResource(R.drawable.icon_customer_tag);

            tv_content1.setText("标签：" + tagItems);
            tv_content4.setText("距离：" + customer.distance);
            if (isNear && customer.winCount != 0) {
                imgWin.setVisibility(View.VISIBLE);
            }
        }

        //附近 - 公司已赢单
        if (customer_type == Customer.CUSTOMER_TYPE_NEAR_COMPANY) {
            layout_go_where.setVisibility(View.VISIBLE);
            img_public.setVisibility(View.GONE);
            layout2.setVisibility(View.GONE);
            layout3.setVisibility(View.GONE);

            img1.setImageResource(R.drawable.img_sign_list_position);
            img2.setImageResource(R.drawable.icon_customer_demands_plan);

            tv_content1.setText("地址：" + customer.loc.addr);

            if (null != customer.distance) {
                String distance;
                if (customer.distance.contains("km")) {
                    tv_content4.setText(df.format(Double.parseDouble(customer.distance.replace("km", ""))) + "km");
                } else if (customer.distance.contains("m")) {
                    double disa = Float.parseFloat(customer.distance.replace("m", ""));
                    if (disa <= 100) {
                        distance = "<0.1km";
                    } else {
                        distance = df.format(disa / 1000) + "km";
                    }
                    tv_content4.setText(distance);
                }
            } else {
                tv_content4.setText("无距离数据");
            }
        }

        /*导航按钮*/
        img_go_where.setOnTouchListener(Global.GetTouch());
        img_go_where.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.goWhere(mContext, customer.loc.loc[1], customer.loc.loc[0], customer.loc.addr);
            }
        });

        return convertView;
    }
}
