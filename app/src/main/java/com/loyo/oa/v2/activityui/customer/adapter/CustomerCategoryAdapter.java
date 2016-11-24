package com.loyo.oa.v2.activityui.customer.adapter;

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
 * 【客户管理】title标签选择器
 */
public class CustomerCategoryAdapter extends BaseAdapter {


    private LayoutInflater layoutInflater;
    public List<String> lstData = new ArrayList<>();
    private int selectPosition = -1;

    public CustomerCategoryAdapter(final Context context, final List<String> lstData) {
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
        String content = lstData.get(position);
        if (!TextUtils.isEmpty(content)) {
            tv.setText(content);
        }

        switch (content){

            case "我的拜访":
            case "我的跟进":
            case "我负责的":
                iv.setBackgroundResource(R.drawable.icon_my);
                break;

            case "我参与的":
                iv.setBackgroundResource(R.drawable.icon_customer_member);
                break;

            case "团队拜访":
            case "团队跟进":
            case "团队客户":
                iv.setBackgroundResource(R.drawable.icon_team);
                break;

            case "公海客户":
                iv.setBackgroundResource(R.drawable.icon_public);
                break;

            default:
                break;
        }

/*        if (position == 0) {
            iv.setBackgroundResource(R.drawable.icon_my);
        } else if (position == 1) {
            if (lstData.get(1).contains("团队")) {
                iv.setBackgroundResource(R.drawable.icon_team);
            } else if (lstData.get(1).contains("分派")) {
                iv.setBackgroundResource(R.drawable.icon_assignment);
            } else {
                iv.setBackgroundResource(R.drawable.icon_public);
            }

        } else if (position == 2) {
            iv.setBackgroundResource(R.drawable.icon_public);
        } else if (position == 3) {
            if (lstData.get(3).contains("团队")) {
                iv.setBackgroundResource(R.drawable.icon_team);
            } else {
                iv.setBackgroundResource(R.drawable.icon_public);
            }
        }*/
        return convertView;
    }

}
