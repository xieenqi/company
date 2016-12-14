package com.loyo.oa.v2.activityui.order.common;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.application.MainApp;

import java.util.Date;

/**
 * 订单详情 自定义字段
 */
public class ViewOrderDetailsExtra extends LinearLayout {

    private View convertView;
    private TextView name;
    private TextView value;
    private ContactLeftExtras mData;

    public ViewOrderDetailsExtra(Context context, ContactLeftExtras data) {
        super(context);
        mData = data;
        bindView(mData);
    }

    public void bindView(ContactLeftExtras mData) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_saledetils_extra, null);
        name = (TextView) convertView.findViewById(R.id.saledetils_name);
        value = (TextView) convertView.findViewById(R.id.saledetils_value);
        name.setText(mData.label + "：");
        if (!TextUtils.isEmpty(mData.val) && "long".equals(mData.type)) {
            value.setText(DateTool.getDateTimeFriendly(Long.parseLong(mData.val)));
        } else {
            value.setText(mData.val);
        }
        addView(convertView);
    }
}
