package com.loyo.oa.v2.activityui.worksheet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetListType;
import com.loyo.oa.v2.tool.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 【公用适配器】  客户管理 销售机会 签到拜访 订单管理
 */
public class WorksheetCategoryAdapter extends BaseAdapter {


    private LayoutInflater layoutInflater;
    public List<WorksheetListType> lstData = new ArrayList<>();
    private int selectPosition = -1;

    public WorksheetCategoryAdapter(final Context context, final List<WorksheetListType> lstData) {
        this.lstData = lstData;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setSelectPosition(final int position) {
        selectPosition = position;
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
        tv.setText(lstData.get(position).getTitle());
        iv.setBackgroundResource(lstData.get(position).getIcon());
        return convertView;
    }

}
