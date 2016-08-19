package com.loyo.oa.v2.activityui.sale.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.bean.Department;

import java.util.ArrayList;


/**
 * 部门选择适配器
 * Created by yyy on 15/12/25.
 */
public class SelectDetAdapter extends BaseAdapter {

    public ArrayList<Department> listDepartment;
    public Context mContext;
    public LayoutInflater mInflater;
    public int selectedPosition;

    public SelectDetAdapter(final Context mContext, final ArrayList<Department> listDepartment) {
        this.mContext = mContext;
        this.listDepartment = listDepartment;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return listDepartment.size();
    }

    @Override
    public Object getItem(final int position) {
        return listDepartment.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    public void setSelectedPosition(final int position) {
        selectedPosition = position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup viewGroup) {

        convertView = mInflater.inflate(R.layout.item_selectcustomer_left_lv, null);
        TextView detName = (TextView) convertView.findViewById(R.id.item_selectdu_left_tv);

        if(listDepartment.get(position).getXpath().split("/").length > 2){
            detName.setTextColor(Color.parseColor("#c9c9c9"));
        }

        detName.setText(listDepartment.get(position).getName());
        if (position == selectedPosition) {
            convertView.setBackgroundResource(R.color.activity_bg);
        } else {
            convertView.setBackgroundResource(R.color.white);
        }

        return convertView;
    }
}
