package com.loyo.oa.v2.activityui.order.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.wfinstance.bean.WfTemplate;

import java.util.ArrayList;
import java.util.List;

public class TerminateProcessListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    List<WfTemplate> data = new ArrayList<>();
    private int selectedIndex;

    public TerminateProcessListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        selectedIndex = -1;
    }

    public void setData(List<WfTemplate> records) {
        data.clear();
        this.data.addAll(records);
        notifyDataSetChanged();
    }

    public WfTemplate getItemData(int index) {
        return data.get(index);
    }

    public void selectItemAtIndex(int index) {
        selectedIndex = index;
    }

    public WfTemplate getSelectItem() {
        if (data != null && selectedIndex >= 0 || selectedIndex < data.size()) {
            return getItemData(selectedIndex);
        }
        return null;
    }

    public void setSelectedItem(WfTemplate item) {
        int index = data.indexOf(item);
        selectItemAtIndex(index);
    }

    @Override
    public int getCount() {
        return null == data ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_process_item_cell, null);
            holder = new Holder();
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        WfTemplate mData = data.get(position);
        holder.tv_title.setText(mData.getTitle());
        holder.checkbox.setChecked(position == selectedIndex);
        return convertView;
    }

    class Holder {
        TextView tv_title;
        CheckBox checkbox;
    }
}
