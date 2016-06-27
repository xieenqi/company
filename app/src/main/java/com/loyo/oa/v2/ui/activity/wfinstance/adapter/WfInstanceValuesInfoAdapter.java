package com.loyo.oa.v2.ui.activity.wfinstance.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.BizFormFields;

import java.util.ArrayList;
import java.util.HashMap;

//显示流程数据的adapter
public class WfInstanceValuesInfoAdapter extends BaseAdapter {

    ArrayList<HashMap<String,String>> mWfInstanceValuesDatas;
    LayoutInflater mLayoutInflater;
    ArrayList<BizFormFields> mFields;

    public WfInstanceValuesInfoAdapter(Context context, ArrayList<HashMap<String,String>> wfInstanceValuesDatas, ArrayList<BizFormFields> fields) {
        this.mWfInstanceValuesDatas = wfInstanceValuesDatas;
        mLayoutInflater = LayoutInflater.from(context);
        if (fields != null) {
            mFields = fields;
        }
    }

    @Override
    public int getCount() {
        return mWfInstanceValuesDatas.size();
    }

    @Override
    public HashMap<String,String> getItem(int position) {
        return mWfInstanceValuesDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Item_info item_info;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_listview_wfinstancevalues_info, null);
            item_info = new Item_info();
            item_info.layout_wfInstanceValuesDatas = (ViewGroup) convertView.findViewById(R.id.layout_wfInstanceValuesDatas);
            convertView.setTag(item_info);
        } else {
            item_info = (Item_info) convertView.getTag();
        }

        item_info.layout_wfInstanceValuesDatas.removeAllViews();

        HashMap<String,String> jsonObject = getItem(position);

        for (int i = 0; i < mFields.size(); i++) {
            BizFormFields field = mFields.get(i);

            View view_value = mLayoutInflater.inflate(R.layout.item_listview_wfinstancevalues_data, null);

            EditText tv_value = (EditText) view_value.findViewById(R.id.et_value);
            tv_value.setEnabled(false);
            tv_value.setText(jsonObject.get(field.getId()));

            TextView tv_key = (TextView) view_value.findViewById(R.id.tv_key);
            tv_key.setText(field.getName());

            item_info.layout_wfInstanceValuesDatas.addView(view_value);
        }

        return convertView;
    }

    class Item_info {
        ViewGroup layout_wfInstanceValuesDatas;
    }
}
