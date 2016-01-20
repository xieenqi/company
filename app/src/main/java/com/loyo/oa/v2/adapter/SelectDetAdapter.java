package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.Department;

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

    public SelectDetAdapter(Context mContext, ArrayList<Department> listDepartment) {
        this.mContext = mContext;
        this.listDepartment = listDepartment;
        mInflater = LayoutInflater.from(mContext);
    }


    public class ViewHolder {
        TextView detName;
    }

    @Override
    public int getCount() {
        return listDepartment.size();
    }

    @Override
    public Object getItem(int position) {
        return listDepartment.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_selectcustomer_left_lv, null);
            holder.detName = (TextView) convertView.findViewById(R.id.item_selectdu_left_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.detName.setText(listDepartment.get(position).getName());
        if (position == selectedPosition) {
            convertView.setBackgroundResource(R.color.beogray);
        } else {
            convertView.setBackgroundResource(R.color.white);
        }
        return convertView;
    }
}
