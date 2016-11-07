package com.loyo.oa.v2.activityui.setting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.loyo.oa.v2.R;

/**
 * Created by xeq on 16/11/7.
 */

public class AdapterSystemMessage extends BaseAdapter {

    LayoutInflater inflater;

    public AdapterSystemMessage(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 30;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (null == convertView) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.item_system_message, null);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        return convertView;
    }

    class Holder {

    }
}
