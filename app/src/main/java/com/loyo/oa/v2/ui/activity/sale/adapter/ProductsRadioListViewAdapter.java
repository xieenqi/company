package com.loyo.oa.v2.ui.activity.sale.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.Product;

import java.util.ArrayList;


public class ProductsRadioListViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Product> lstData;
    public long isSelected = -1;

    public ProductsRadioListViewAdapter(final Context context, final ArrayList<Product> lstData) {
        mInflater = LayoutInflater.from(context);
        this.lstData = lstData;
    }

    @Override
    public int getCount() {
        return lstData.size();
    }

    @Override
    public Object getItem(final int position) {
        return lstData.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        Item_info item_info = null;
        //convertView为null的时候初始化convertView。
        if (convertView == null) {
            item_info = new Item_info();
            convertView = mInflater.inflate(R.layout.item_listview_product_select, null,false);
            item_info.tv = (TextView) convertView.findViewById(R.id.tv);
            convertView.setTag(item_info);
        } else {
            item_info = (Item_info) convertView.getTag();
        }

        Product product = lstData.get(position);
        if (product != null) {
            item_info.tv.setText(product.name);
        }

        return convertView;
    }

    public class Item_info {
        public TextView tv;
    }
}
