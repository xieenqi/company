package com.loyo.oa.v2.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.GroupsData;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.ViewHolder;

/**
 * Created by EthanGong on 16/8/27.
 */
public abstract class BaseGroupsDataAdapter extends BaseExpandableListAdapter {
    protected GroupsData groupsData;
    protected Context mContext;
    protected MainApp app;

    public BaseGroupsDataAdapter() {
        app = MainApp.getMainApp();
    }

    public void setData(final GroupsData data) {
        groupsData = data;
    }

    public GroupsData getData() {
        return groupsData;
    }

    @Override
    public int getGroupCount() {
        return groupsData.size();
    }

    @Override
    public int getChildrenCount(final int groupPosition) {
        return groupsData.get(groupPosition).size();
    }

    @Override
    public Object getGroup(final int groupPosition) {
        return groupsData.get(groupPosition);
    }

    @Override
    public Object getChild(final int groupPosition, final int childPosition) {
        return groupsData.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(final int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(final int groupPosition, final int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sign_show_group, null);
        }
        TextView tv_title = ViewHolder.get(convertView, R.id.tv_title);
        ImageView img_status = ViewHolder.get(convertView, R.id.img_time_point);
        GroupsData.SectionData data = null;
        if (groupPosition >= 0)
            data = groupsData.get(groupPosition);

        /*列表状态条 颜色设置*/
        if ( data != null ) {
            tv_title.setText(data.groupKey.getName());
            tv_title.setTextColor(mContext.getResources().getColor(data.groupKey.getColor()));
            img_status.setImageResource(data.groupKey.getIcon());
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(final int groupPosition, final int childPosition) {
        return true;
    }

    public abstract View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent);
}
