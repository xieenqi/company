package com.loyo.oa.v2.activityui.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.activityui.customer.model.Item_info_Group;
import com.loyo.oa.v2.beans.PagingGroupData;

import java.util.ArrayList;
import java.util.List;

public abstract class BasePagingGroupDataAdapter<T extends BaseBeans> extends BaseExpandableListAdapter {

    protected ArrayList<PagingGroupData<T>> pagingGroupDatas;
    protected Context mContext;
    protected MainApp app;

    public BasePagingGroupDataAdapter() {
        app = MainApp.getMainApp();
    }

    public void setData(final ArrayList<PagingGroupData<T>> data) {
        pagingGroupDatas = data;
    }
    //上拉加载时不会滑到第一页需要获取数据源 ykb 07-15
    public List<PagingGroupData<T>> getData() {
        return pagingGroupDatas;
    }

    @Override
    public int getGroupCount() {
        return pagingGroupDatas.size();
    }

    @Override
    public int getChildrenCount(final int groupPosition) {
        return pagingGroupDatas.get(groupPosition).getRecords().size();
    }

    @Override
    public Object getGroup(final int groupPosition) {
        return pagingGroupDatas.get(groupPosition);
    }

    @Override
    public Object getChild(final int groupPosition, final int childPosition) {
        return pagingGroupDatas.get(groupPosition).getRecords().get(childPosition);
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
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sign_show_group, null);
            item_info_Group = new Item_info_Group();
            item_info_Group.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(item_info_Group);
        } else {
            item_info_Group = (Item_info_Group) convertView.getTag();
        }

        PagingGroupData data = pagingGroupDatas.get(groupPosition);
        if (data != null && data.getTime() != null) {
            item_info_Group.tv_title.setText(data.getTime());
        }

        return convertView;
    }

    Item_info_Group item_info_Group;

    @Override
    public boolean isChildSelectable(final int groupPosition, final int childPosition) {
        return true;
    }

    public abstract View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent);
}
