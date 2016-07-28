package com.loyo.oa.v2.activityui.wfinstance.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 审批列表 title 选项列表
 */
public class WinstanceCategoryAdapter extends BaseAdapter {


    private LayoutInflater layoutInflater;
    public List<String> lstData = new ArrayList<>();

    public WinstanceCategoryAdapter(final Context context, final List<String> lstData) {
        this.lstData = lstData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return null == lstData ? 0 : lstData.size();
    }

    @Override
    public Object getItem(final int position) {

        return null == lstData || lstData.isEmpty() ? null : lstData.get(position);
    }

    @Override
    public long getItemId(final int position) {

        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_wf_category_layout, parent, false);
        }
        TextView tv = ViewHolder.get(convertView, R.id.item_wf_category_tv);
        ImageView iv = ViewHolder.get(convertView, R.id.iv_head);
        String content = lstData.get(position);
        if (!TextUtils.isEmpty(content)) {
            tv.setText(content);
        }
        if (position == 0) {
            iv.setBackgroundResource(R.drawable.icon_my_submit);
        } else if (position == 1) {
            iv.setBackgroundResource(R.drawable.icon_my_approve);
        }
        return convertView;
    }

}
