package com.loyo.oa.v2.customview;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.customer.bean.ContactLeftExtras;

import java.util.Date;

/**
 * 机会详情 自定义字段View
 * Created by yyy on 16/5/23.
 */
public class ViewSaleDetailsExtra extends LinearLayout {

    private View convertView;
    private TextView name;
    private TextView value;
    private ContactLeftExtras mData;

    public ViewSaleDetailsExtra(Context context, ContactLeftExtras data) {
        super(context);
        mData = data;
        bindView(mData);
    }

    public void bindView(ContactLeftExtras mData) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_saledetils_extra, null);
        name = (TextView) convertView.findViewById(R.id.saledetils_name);
        value = (TextView) convertView.findViewById(R.id.saledetils_value);
        name.setText(mData.label + "：");
        if (!TextUtils.isEmpty(mData.val)&&"long".equals(mData.type)) {
            value.setText(MainApp.getMainApp().df3.format(new Date(Long.valueOf(mData.val) * 1000)));
        } else {
            value.setText(mData.val);
        }
        addView(convertView);
    }
}
