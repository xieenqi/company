package com.loyo.oa.v2.tool.customview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.sale.bean.SaleDetails;

/**
 * 机会详情 自定义字段View
 * Created by yyy on 16/5/23.
 */
public class ViewSaleDetailsExtra extends LinearLayout{

    private View convertView;
    private TextView name;
    private TextView value;
    private SaleDetails.SaleDetailsExtraList mData;

    public ViewSaleDetailsExtra(Context context,SaleDetails.SaleDetailsExtraList data) {
        super(context);
        mData = data;
        bindView(mData);
    }

    public void bindView(SaleDetails.SaleDetailsExtraList mData){
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_saledetils_extra,null);
        name = (TextView) convertView.findViewById(R.id.saledetils_name);
        value = (TextView) convertView.findViewById(R.id.saledetils_value);
        name.setText(mData.getProperties().getLabel()+":");
        value.setText(mData.getVal());
        addView(convertView);
    }
}
